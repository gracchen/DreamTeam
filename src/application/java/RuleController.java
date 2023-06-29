package application.java;

import java.sql.SQLException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.IntegerStringConverter;

public class RuleController {
	private Connect c;
	@FXML private BorderPane view;

	private String tableName = "Rules";
	@FXML private Button addButton;
	@FXML private Button delButton;
	@FXML private TableColumn<Rule, String> nameCol;
	@FXML private TableColumn<Rule, Boolean> monCol;
	@FXML private TableColumn<Rule, Boolean> tuesCol;
	@FXML private TableColumn<Rule, Boolean> wedCol;
	@FXML private TableColumn<Rule, Boolean> thursCol;
	@FXML private TableColumn<Rule, Boolean> friCol;
	@FXML private TableColumn<Rule, Boolean> satCol;
	@FXML private TableColumn<Rule, Boolean> sunCol;
	@FXML private TableView<Rule> tableview;

	ObservableList<Rule> list = FXCollections.observableArrayList();
	
	private Boolean mouseOut = true;

	@FXML
	private void initialize() {
		//LAYOUT binding

		//bind Rule's col width to remaining width of tableview minus percent col (fixed at 30)
		nameCol.prefWidthProperty().bind(tableview.widthProperty().subtract(30*7));

		initTable();
	}

	void initTable() {		
		//set data to display
		nameCol.setCellValueFactory(new PropertyValueFactory<Rule,String>("name"));
		monCol.setCellValueFactory(new PropertyValueFactory<Rule,Boolean>("mon"));
		tuesCol.setCellValueFactory(new PropertyValueFactory<Rule,Boolean>("tues"));
		wedCol.setCellValueFactory(new PropertyValueFactory<Rule,Boolean>("wed"));
		thursCol.setCellValueFactory(new PropertyValueFactory<Rule,Boolean>("thurs"));
		friCol.setCellValueFactory(new PropertyValueFactory<Rule,Boolean>("fri"));
		satCol.setCellValueFactory(new PropertyValueFactory<Rule,Boolean>("sat"));
		sunCol.setCellValueFactory(new PropertyValueFactory<Rule,Boolean>("sun"));
		
		//make editable
		nameCol.setCellFactory(TextFieldTableCell.<Rule>forTableColumn());
		nameCol.setOnEditCommit(e -> {
			c.runSQL("update " + tableName + " set name = \'" + e.getNewValue().replaceAll("'", "''") + "\' where id = " + e.getRowValue().getId());
			e.getRowValue().setName(e.getNewValue());
		});
		monCol.setCellFactory(CheckBoxTableCell.forTableColumn(monCol));
		tuesCol.setCellFactory(CheckBoxTableCell.forTableColumn(tuesCol));
		wedCol.setCellFactory(CheckBoxTableCell.forTableColumn(wedCol));
		thursCol.setCellFactory(CheckBoxTableCell.forTableColumn(thursCol));
		friCol.setCellFactory(CheckBoxTableCell.forTableColumn(friCol));
		satCol.setCellFactory(CheckBoxTableCell.forTableColumn(satCol));
		sunCol.setCellFactory(CheckBoxTableCell.forTableColumn(sunCol));

		tableview.setItems(list);
		
	}
	
	void highlight(int menuID) { 
		tableview.getSelectionModel().clearSelection(); // Clear any existing selections

		// Iterate over the rows and select the ones with matching menuID
		for (Rule rowData : tableview.getItems()) {
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

	void loadData() {
		list.clear();
		c.runSQL("select * from Rules;");		
		//int id, String name, int menuID, int progress, String link
		try {
			while(c.rs.next()) {
				list.add(new Rule(c.rs.getInt("id"),c.rs.getString("name"),c.rs.getInt("menuID"),  c.rs.getBoolean("mon"), c.rs.getBoolean("tues"), 
						c.rs.getBoolean("wed"), c.rs.getBoolean("thurs"), c.rs.getBoolean("fri"), c.rs.getBoolean("sat"), c.rs.getBoolean("sun"), c));
			}
		} catch (SQLException e1) {e1.printStackTrace();}
	}

	Boolean addEntry(int menuID, String name) {
		c.runSQL(String.format("insert into %s (name, menuID, mon, tues, wed, thurs, fri, sat, sun) values ('%s', '%d',0,0,0,0,0,0,0);", tableName, name.replaceAll("'","''"), menuID));	
		c.runSQL(String.format("select * from %s order by id desc limit 1", tableName));	//get id of the last added row
		try {
			if (c.rs.next()) {
				list.add(new Rule(c.rs.getInt("id"), name, menuID, false, false, false, false, false, false, false, c));
				tableview.scrollTo(list.get(list.size()-1));
				return true;
			}
		} catch (SQLException e1) {e1.printStackTrace();}
		return false;
	}
	
	void updateEntry(int menuID, String newName) {
		for (Rule rule : list) {
			if (rule.getMenuID() == menuID) {
				rule.setName(newName);
				c.runSQL("update Rules set name = '" + newName.replaceAll("'","''") + "' where id = " + rule.getId()); //and remotely
			}
		}
	}

	@FXML
	void addClicked(ActionEvent event) {
		if (addEntry(-1,"")) { //if adding successful
			//puts user in edit mode for newly added (last) row's name
			int lastRowIndex = tableview.getItems().size() - 1;	
			tableview.edit(lastRowIndex, nameCol);
			tableview.scrollTo(list.get(lastRowIndex)); //causes buggy behavior
		}
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
			int menuID = Integer.valueOf(parts[0]);
			for (Rule rule : list) {
				if (rule.getMenuID() == menuID) {
					System.out.println("Already in rules table.");
					return; //don't duplicate
				}
			}
			addEntry(menuID, parts[1]);
		}
		event.setDropCompleted(success);
		event.consume();
	}

	
	@FXML
	void delClicked(ActionEvent event) {
		if (tableview.getSelectionModel().getSelectedIndices().isEmpty()) return;
		ObservableList<Rule> selectedItems = tableview.getSelectionModel().getSelectedItems();
		StringBuilder idList = new StringBuilder(String.valueOf(selectedItems.get(0).getId()));	//start with first id
		for (int i = 1; i < selectedItems.size(); i++) {
			Rule Rule = selectedItems.get(i);
			idList.append(",");
			idList.append(String.valueOf(Rule.getId()));
		}
		c.runSQL(String.format("delete from %s where id in(%s);", tableName, idList));
		list.removeAll(selectedItems);
	}   

	@FXML
	void enterDayPane(MouseEvent event) {
		mouseOut = false;
	}

	@FXML
	void exitDayPane(MouseEvent event) {
		mouseOut = true;
	}

	Boolean getMouseOut() {
		return mouseOut;
	}

	void setConnect(Connect c) {
		this.c = c;
		loadData();
	}
}
