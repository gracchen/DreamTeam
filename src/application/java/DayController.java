package application.java;

import java.sql.SQLException;
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
	@FXML
	private BorderPane dayPane;
	@FXML
	private Label header;
	private StringProperty tableName = new SimpleStringProperty();
	@FXML
	private Button addButton;
	@FXML
	private Button delButton;
	@FXML
	private TableColumn<Task, String> taskCol;
	@FXML
	private TableColumn<Task, Integer> percentCol;
	@FXML
	private TableView<Task> tableview;
	private Main m;
	private LocalDate day;
	ObservableList<Task> list = FXCollections.observableArrayList();
	ObservableList<Task> linkStyledCells = FXCollections.observableArrayList();
	private Boolean mouseOut = true;

	@FXML
	private void initialize() {
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

	public void setMain(Main m) {
		this.m = m;
	}
	public void deleteMenuID(int menuID) {
		// Iterate over the rows and select the ones with matching menuID
		c.runSQL("delete from MasterTasks where menuID = " + menuID);
		ObservableList<Task> toDelete = FXCollections.observableArrayList();
		for (Task rowData : tableview.getItems()) {
			if (rowData.getMenuID() == menuID) {
				toDelete.add(rowData);
			}
		}
		list.removeAll(toDelete);
		deselect();
	}
	void initTable() {		
		//set data to display
		percentCol.setCellValueFactory(new PropertyValueFactory<Task,Integer>("progress"));
		taskCol.setCellValueFactory(new PropertyValueFactory<Task,String>("name"));

		//set editable using textfield and built in integer conversion
		percentCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		//taskCol.setCellFactory(TextFieldTableCell.<Task>forTableColumn());
		taskCol.setCellFactory(column -> {
			TableCell<Task, String> cell = new TableCell<Task, String>() {
				private TextField textField;
				private ToolTip tooltip = new ToolTip("");

				@Override
				public void updateItem(String item, boolean empty) {

					//handle right click to add that allows user to insert link, then updates tooltip link
					setOnMouseClicked(event -> {
						if (!isEmpty() && event.getButton() == MouseButton.SECONDARY) {
							Task target = list.get(getIndex());
							TextInputDialog dialog = new TextInputDialog(target.getLink());
							dialog.setTitle("Link Insertion");
							dialog.setHeaderText("Enter a link:");
							Optional<String> result = dialog.showAndWait();

							result.ifPresent(input -> {
								if (input.length() == 0) {
									target.setLink(null);
									c.runSQL(String.format("update MasterTasks set link=NULL where id=%d;", target.getId()));
									putLink(null);
								}
								else {
									target.setLink(input);
									c.runSQL(String.format("update MasterTasks set link='%s' where id=%d;", input.replaceAll("'", "''"), target.getId()));
									putLink(target.getLink());
								}
							});
						}
					});

					super.updateItem(item, empty);

					if (empty || item == null) {
						setText(null);
						setTooltip(null);
					} 
					else {
						System.out.println("Updating " + item);
						setText(item);
						String link = getTableView().getItems().get(getIndex()).getLink();
						if (link == null) setTooltip(null);
						else putLink(link);
					}
				}

				public void putLink(String link) {
					if (link == null) {
						System.out.println("removing link!");
						setTooltip(null); //removes link
						if (getStyleClass().contains("link")) { //but still styled as a link
							getStyleClass().removeAll("link");
							setStyle("");
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
					super.startEdit();
					if (isEmpty()) {
						return;
					}
					if (textField == null) {
						createTextField();
					}
					textField.setText(getItem()); // Set initial text
					setText(null);
					setGraphic(textField);
					setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
					textField.requestFocus();
				}

				@Override
				public void cancelEdit() {
					super.cancelEdit();
					setText(getItem());
					setGraphic(null);
					setContentDisplay(ContentDisplay.TEXT_ONLY);
				}

				@Override
				public void commitEdit(String newValue) {
					super.commitEdit(newValue);
					setText(newValue);
					setGraphic(null);
					setContentDisplay(ContentDisplay.TEXT_ONLY);
				}

				private void createTextField() {
					textField = new TextField(getItem());
					textField.setOnKeyPressed(event -> {
						if (event.getCode() == KeyCode.ENTER) {
							commitEdit(textField.getText());
							event.consume();
						}
					});
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

	public void highlight(int menuID) {
		tableview.getSelectionModel().clearSelection(); // Clear any existing selections

		// Iterate over the rows and select the ones with matching menuID
		for (Task rowData : tableview.getItems()) {
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
	public void reset() {
		tableview.getItems().clear();
		tableview.refresh();
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
			addEntry(parts[1], Integer.valueOf(parts[0]));
		}
		event.setDropCompleted(success);
		event.consume();
	}

	void loadData() {
		list.clear();
		c.runSQL(String.format("select * from MasterTasks where day='%s';", day));		
		//int id, String name, int menuID, int progress, String link
		try {
			while(c.rs.next()) {
				list.add(new Task(c.rs.getInt("id"),c.rs.getString("name"), c.rs.getInt("menuID"), c.rs.getInt("progress"), c.rs.getString("link")));
			}
		} catch (SQLException e1) {e1.printStackTrace();}
	}

	Boolean addEntry(String name, int menuID) {
		c.runSQL(String.format("insert into MasterTasks (day, name, menuID, progress) values ('%s','%s','%d','0');", day, name.replaceAll("'", "''"), menuID));	
		c.runSQL("select * from MasterTasks order by id desc limit 1");	//get id of the last added row
		try {
			if (c.rs.next()) {
				list.add(new Task(c.rs.getInt("id"), name, menuID, 0, null));
				tableview.scrollTo(list.get(list.size()-1));
				return true;
			}
		} catch (SQLException e1) {e1.printStackTrace();}
		return false;
	}

	Boolean addEntry(String name) {
		return addEntry(name, -1);
	}

	@FXML
	void addClicked(ActionEvent event) {
		if (addEntry("")) { //if adding successful
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
		ObservableList<Task> selectedItems = tableview.getSelectionModel().getSelectedItems();
		StringBuilder idList = new StringBuilder(String.valueOf(selectedItems.get(0).getId()));	//start with first id
		for (int i = 1; i < selectedItems.size(); i++) {
			Task task = selectedItems.get(i);
			idList.append(",");
			idList.append(String.valueOf(task.getId()));
		}
		c.runSQL(String.format("delete from MasterTasks where id in(%s);", idList));
		list.removeAll(selectedItems);
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
		for (Task rowData : tableview.getItems()) {
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
		tableName.set(name + day); //handle for main controller to tell me what table to pull from
		this.c = c;
		loadData();
	}

	Boolean hasTask(int menuID) {

		for (Task task : list) {
			if (task.getMenuID() == menuID) {
				return true;
			}
		}
		return false;
	}
}
