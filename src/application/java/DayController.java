package application.java;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.IntegerStringConverter;

public class DayController {
	private Connect c;
	@FXML private BorderPane dayPane;
	@FXML private Label header;
	private StringProperty tableName = new SimpleStringProperty();
	@FXML private Button addButton;
	@FXML private Button delButton;
	@FXML private TableColumn<MyTask, String> taskCol;
	@FXML private TableColumn<MyTask, Integer> percentCol;
	@FXML private TableView<MyTask> tableview;
	private Main m;
	private LocalDate day, Monday, curMonday;
	private ObservableList<MyTask> list = FXCollections.observableArrayList();
	private Boolean mouseOut = true;
	private Controller ctrl;

	@FXML private void initialize() {
		//LAYOUT binding

		//bind task's col width to remaining width of tableview minus percent col (fixed at 30)
		taskCol.prefWidthProperty().bind(tableview.widthProperty().subtract(30));

		//bind header to name of table
		header.textProperty().bind(tableName);

		//draw box around it so that aligns centered with tableview
		header.prefWidthProperty().bind(tableview.widthProperty());
		header.setStyle("-fx-border-width: 1 0.5 1 0.5; -fx-border-color: black;");
		header.setAlignment(Pos.CENTER);

		//binds add and delete buttons to shortened text to avoid truncation.
		addButton.widthProperty().addListener((ChangeListener<? super Number>) new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> obs, Number oldVal, Number newVal) {
				addButton.setText(newVal.doubleValue() < 46 ? "+" : "Add");
				delButton.setText(newVal.doubleValue() < 56 ? (newVal.doubleValue() < 46 ? "-" : "Del") : "Delete");
			}
		});

		initTable();
	}

	public void setMain(Main m, Controller ctrl) {
		this.m = m;
		this.ctrl = ctrl;
	}
	
	public void highlightCal() {
		if (!Monday.equals(curMonday)) {
			//System.out.println("highlight cal time!");
			ctrl.highlightCal(day);
		}
	}
	public void unhighlightCal() {
		if (!Monday.equals(curMonday)) {
			//System.out.println("highlight cal time!");
			ctrl.unhighlightCal(day);
		}
	}

	public void deleteMenuID(int menuID) {
		// Iterate over the rows and select the ones with matching menuID
		c.runSQL("delete from MasterTasks where menuID = " + menuID);
		ObservableList<MyTask> toDelete = FXCollections.observableArrayList();
		for (MyTask rowData : tableview.getItems()) {
			if (rowData.getMenuID() == menuID) {
				toDelete.add(rowData);
			}
		}
		list.removeAll(toDelete);
		deselect();
	}

	void initTable() {		
		//set data to display
		percentCol.setCellValueFactory(new PropertyValueFactory<MyTask,Integer>("progress"));
		taskCol.setCellValueFactory(new PropertyValueFactory<MyTask,String>("name"));

		//set editable using textfield and built in integer conversion
		percentCol.setCellFactory(column -> new TextFieldTableCell<MyTask, Integer>(new IntegerStringConverter()) {
		    @Override
		    public void updateItem(Integer item, boolean empty) {
		        super.updateItem(item, empty);
		        
		        if (empty || item == null) {
		            setText(null);
		        } else {
		            setText(String.valueOf(item));
		            
		            TableRow<MyTask> row = getTableRow();
		            if (row != null) {
		                MyTask myTask = row.getItem();
		                if (myTask != null) {
		                    // Perform custom styling or other operations based on task or row data
		                    if (myTask.getProgress() < 100 && LocalDate.now().isAfter(day)) {
		                        setStyle("-fx-text-fill: red;");
		                    } else if (myTask.getProgress() == 100){
		                        setStyle("-fx-text-fill: blue;");
		                    } else {
		                    	setStyle("-fx-text-fill: black;");
		                    }
		                }
		            }
		        }
		    }
		});

		//percentCol.setCellFactory(column -> new CustomCell());

		//taskCol.setCellFactory(TextFieldTableCell.<MyTask>forTableColumn());
		taskCol.setCellFactory(column -> {
			TableCell<MyTask, String> cell = new TableCell<MyTask, String>() {
				private TextField textField;
				private ToolTip tooltip = new ToolTip("");

				@Override
				public void updateItem(String item, boolean empty) {
					//System.out.println(String.format("updateItem(\"%s\",%s);", item, ((empty)? "true" : "false")));
					//handle right click to add that allows user to insert link, then updates tooltip link

					super.updateItem(item, empty);

					if (empty || item == null) {
						setText(null);
						setTooltip(null);
						//System.out.println("updateItem! empty line 184");
					} 
					else {
						//System.out.println("updateItem! nonempty line 187");
						setText(item);
						//System.out.println("updateItem! line 189");
						MyTask target = getTableView().getItems().get(getIndex());
						//System.out.println("updateItem! line 191");
						String link = target.getLink();
						//System.out.println("updateItem! line 193");
						if (link == null) setTooltip(null);
						else putLink(link);
						//System.out.println("updateItem! line 196");
						setOnMouseClicked(event -> {
							//System.out.println("updateItem! line 155");
							if (!isEmpty() && event.getButton() == MouseButton.SECONDARY) {
								TextInputDialog dialog = new TextInputDialog(target.getLink());
								dialog.setTitle("Link Insertion");
								dialog.setHeaderText("Enter a link:");
								Optional<String> result = dialog.showAndWait();

								result.ifPresent(input -> {
									if (input.length() == 0) {
										list.get(getIndex()).setLink(null);
										c.runSQL(String.format("update MasterTasks set link=NULL where id=%d;", target.getId()));
										putLink(null);
									}
									else {
										list.get(getIndex()).setLink(input);
										c.runSQL(String.format("update MasterTasks set link='%s' where id=%d;", input.replaceAll("'", "''"), target.getId()));
										putLink(target.getLink());
									}
								});
							}
						});
					}
				}

				public void putLink(String link) {
					if (link == null) {
						System.out.println("removing link!");
						setTooltip(null); //removes link
						if (getStyleClass().contains("link")) { //but still styled as a link
							getStyleClass().removeAll("link");
							setStyle(null);
						}
						return;
					}

					Hyperlink hyperlink = new Hyperlink(link);

					hyperlink.setOnAction(e -> {
						m.getHostServices().showDocument(link);
						tooltip.hide();
					});

					tooltip.setGraphic(hyperlink);
					setTooltip(tooltip); //add link
					if (!getStyleClass().contains("link")) { //but still not styled as a link
						getStyleClass().add("link");
						setStyle("-fx-underline: true;");
					}
					
				}

				@Override
				public void startEdit() {
					//System.out.println("startEdit(); - line 223");
					super.startEdit();
					if (isEmpty()) {
						return;
					}
					//System.out.println("startEdit(); - line 227");
					if (textField == null) {
						createTextField();
						//System.out.println("startEdit(); - line 230");
					}
					textField.setText(getItem()); // Set initial text
					//System.out.println("startEdit(); - line 233");
					setText(null);
					//System.out.println("startEdit(); - line 235");
					setGraphic(textField);
					//System.out.println("startEdit(); - line 237");
					setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
					//System.out.println("startEdit(); - line 239");
					textField.requestFocus();
					//System.out.println("startEdit(); - line 241");
				}

				@Override
				public void cancelEdit() {
					//System.out.println("cancelEdit(); - line 246");
					super.cancelEdit();
					//System.out.println("cancelEdit(); - line 248");
					setText(getItem());
					//System.out.println("cancelEdit(); - line 250");
					setGraphic(null);
					//System.out.println("cancelEdit(); - line 252");
					setContentDisplay(ContentDisplay.TEXT_ONLY);
					//System.out.println("cancelEdit(); - line 254");
				}

				@Override
				public void commitEdit(String newValue) {
					//System.out.println("commitEdit("+newValue+"); - line 246");
					super.commitEdit(newValue);
					//System.out.println("commitEdit("+newValue+"); - line 248");
					setText(newValue);
					//System.out.println("commitEdit("+newValue+"); - line 250");
					setGraphic(null);
					//System.out.println("commitEdit("+newValue+"); - line 252");
					setContentDisplay(ContentDisplay.TEXT_ONLY);
					//System.out.println("commitEdit("+newValue+"); - line 254");
				}

				private void createTextField() {
					//System.out.println("createTextField(); - line 266");
					textField = new TextField(getItem());
					//System.out.println("createTextField(); - line 268");
					textField.setOnKeyPressed(event -> {
						if (event.getCode() == KeyCode.ENTER) {
							commitEdit(textField.getText());
							event.consume();
						}
					});
					//System.out.println("createTextField(); - line 275");
				}
			};
			return cell;
		});

		//edit listener to execute custom mysql updating code
		taskCol.setOnEditCommit(e -> {
			c.runSQL("update MasterTasks set name = \'" + e.getNewValue().replaceAll("'", "''") + "\' where id = " + e.getRowValue().getId());
			e.getRowValue().setName(e.getNewValue());
		});

		percentCol.setOnEditCommit(e -> {
			int val = e.getNewValue();

			if (val < 0 || val > 100) {	//% out of range
				e.getRowValue().setProgress(e.getOldValue()); //don't touch sql, undo user's change
				loadData();
			}
			else { //commit to sql
				e.getRowValue().setProgress(val);
				c.runSQL("update MasterTasks set progress = \'" + val + "\' where id = " + e.getRowValue().getId());
			}
		});

		tableview.setItems(list);
		//allow multi selection for quicker delete
		tableview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	public void highlightMenu(int menuID) {
		tableview.getSelectionModel().clearSelection(); // Clear any existing selections

		// Iterate over the rows and select the ones with matching menuID
		for (MyTask rowData : tableview.getItems()) {
			//System.out.println("matches" + menuID + "? " + rowData.getName() + " " + rowData.getMenuID());
			if (rowData.getMenuID() == menuID) {
				Platform.runLater(new Runnable() {
					public void run() {
						tableview.getSelectionModel().select(rowData);
					}
				});
			}
		}
	}
	
	public void highlightRule(long l) {
		tableview.getSelectionModel().clearSelection(); // Clear any existing selections

		// Iterate over the rows and select the ones with matching menuID
		for (MyTask rowData : tableview.getItems()) {
			//System.out.println("matches" + menuID + "? " + rowData.getName() + " " + rowData.getMenuID());
			if (rowData.getRuleID() == l) {
				Platform.runLater(new Runnable() {
					public void run() {
						tableview.getSelectionModel().select(rowData);
					}
				});
			}
		}
	}

	public void resetRules(Connect f) {
		dayPane.setDisable(true);
		f.runSQL(String.format("delete from MasterTasks where ruleID != -1 and day = '%s';", day));
		tableview.getItems().clear();
		loadData(f);
		tableview.refresh();
		dayPane.setDisable(false);
	}

	@FXML
	public void dragOver(DragEvent event) {
		if (event.getGestureSource() != tableview && event.getDragboard().hasContent(DataFormat.PLAIN_TEXT))
			event.acceptTransferModes(TransferMode.COPY);
		event.consume();
	}

	@FXML
	public void drop(DragEvent event) { 
		Dragboard db = event.getDragboard();
		//boolean success = db.hasString();
		boolean success = db.hasContent(DataFormat.PLAIN_TEXT);
		if (success) {
			String src = (String) db.getContent(DataFormat.PLAIN_TEXT);
			String[] parts = src.split(":", 2);
			addEntry(parts[1], Integer.valueOf(parts[0]), -1);
		}
		event.setDropCompleted(success);
		event.consume();
	}

	void loadData(Connect f) {
		dayPane.setDisable(true);
		list.clear();
		f.runSQL(String.format("select * from MasterTasks where day='%s';", day));		
		//int id, String name, int menuID, int progress, String link
		try {
			while(f.rs.next()) {
				list.add(new MyTask(f.rs.getInt("id"),f.rs.getString("name"), f.rs.getInt("menuID"), f.rs.getInt("ruleID"), f.rs.getInt("progress"), f.rs.getString("link")));
			}
		} catch (SQLException e1) {System.err.println("dayController.loadData() error :("); e1.printStackTrace();dayPane.setDisable(false);}
		dayPane.setDisable(false);
	}
	
	void loadData() {
		loadData(this.c);
	}

	void sqlAddEntry(String name, int menuID, long l, Connect f) {
		f.runSQL(String.format("insert into MasterTasks (day, name, menuID, ruleID, progress) values ('%s','%s','%d','%d','0');", day, name.replaceAll("'", "''"), menuID, l));
	}
	
	Boolean addEntry(String name, int menuID, int ruleID) {
		//System.out.println("addEntry: line 401");
		c.runSQL(String.format("insert into MasterTasks (day, name, menuID, ruleID, progress) values ('%s','%s','%d','%d','0');", day, name.replaceAll("'", "''"), menuID, ruleID));	
		c.runSQL("select * from MasterTasks order by id desc limit 1");	//get id of the last added row
		try {
			if (c.rs.next()) {
				//System.out.println("addEntry: line 406");
				list.add(new MyTask(c.rs.getInt("id"), name, menuID, ruleID, 0, null));
				//System.out.println("addEntry: line 408");
				tableview.scrollTo(list.get(list.size()-1));
				//System.out.println("addEntry: line 410");
				highlightCal();
				//System.out.println("addEntry: line 412");
				return true;
			}
		} catch (SQLException e1) {System.err.println(String.format("dayController.addEntry(%s,%d,%d) error :(", name, menuID, ruleID)); e1.printStackTrace();}
		
		return false;
	}

	@FXML
	void addClicked(ActionEvent event) {
		if (addEntry("", -1, -1)) { //if adding successful
			deselect();
			//puts user in edit mode for newly added (last) row's name
			int lastRowIndex = tableview.getItems().size() - 1;	
			tableview.edit(lastRowIndex, taskCol);
			tableview.scrollTo(list.get(lastRowIndex)); //causes buggy behavior
		}
	}

	void highlight() {
		header.setStyle("-fx-border-color: orange;");
	}

	@FXML
	void delClicked(ActionEvent event) {
		if (tableview.getSelectionModel().getSelectedIndices().isEmpty()) return;
		ObservableList<MyTask> selectedItems = tableview.getSelectionModel().getSelectedItems();
		StringBuilder idList = new StringBuilder(String.valueOf(selectedItems.get(0).getId()));	//start with first id
		for (int i = 1; i < selectedItems.size(); i++) {
			MyTask myTask = selectedItems.get(i);
			idList.append(",");
			idList.append(String.valueOf(myTask.getId()));
		}
		c.runSQL(String.format("delete from MasterTasks where id in(%s);", idList));
		list.removeAll(selectedItems);
		if (list.isEmpty()) {
			unhighlightCal();
		}
	}   

	void setObservableList(ObservableList<MyTask> l) {
		this.list = l;
	}
	
	@FXML
	void enterDayPane(MouseEvent event) {
		mouseOut = false;
	}

	void deselect() {
		tableview.getSelectionModel().clearSelection();
	}

	public void editMenu(int menuID, String newVal) {
		c.runSQL("update MasterTasks set name = \'" + newVal.replaceAll("'", "''") + "\' where menuID = " + menuID);
		for (MyTask rowData : tableview.getItems()) {
			if (rowData.getMenuID() == menuID) {
				rowData.setName(newVal);
				tableview.getSelectionModel().select(rowData);
			}
		}
	}

	@FXML
	void exitDayPane(MouseEvent event) {
		mouseOut = true;
	}

	Boolean getMouseOut() {
		return mouseOut;
	}

	void initializeVals(String name, LocalDate day, Connect c) {
		this.day = day;
		this.Monday = day;
		this.curMonday = LocalDate.now();
		while (!Monday.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
			Monday = Monday.minusDays(1);
		}
		//System.out.println("This day's monday is : "+ Monday);
		while (!curMonday.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
			curMonday = curMonday.minusDays(1);
		}
		//System.out.println("Today's monday is : "+ curMonday);
		tableName.set(name + day); //handle for main controller to tell me what table to pull from
		this.c = c;
		//loadData();
	}

	Boolean hasTask(int menuID) {

		for (MyTask myTask : list) {
			if (myTask.getMenuID() == menuID) {
				return true;
			}
		}
		return false;
	}

	public int getNumEntries() {
		return list.size();
	}
	
	Boolean hasRules() {

		for (MyTask myTask : list) {
			if (myTask.getRuleID() != -1) {
				return true;
			}
		}
		return false;
	}
}
