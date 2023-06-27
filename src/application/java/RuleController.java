package application.java;

import java.sql.SQLException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.IntegerStringConverter;

public class RuleController {
	private Connect c;
	@FXML
	private BorderPane view;

	private String tableName = "Mon";
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

	ObservableList<Task> list = FXCollections.observableArrayList();
	
	private Boolean mouseOut = true;

	@FXML
	private void initialize() {
		//LAYOUT binding

		//bind task's col width to remaining width of tableview minus percent col (fixed at 30)
		taskCol.prefWidthProperty().bind(tableview.widthProperty().subtract(30));

		initTable();
	}

	void initTable() {		
		//set data to display
		percentCol.setCellValueFactory(new PropertyValueFactory<Task,Integer>("progress"));
		taskCol.setCellValueFactory(new PropertyValueFactory<Task,String>("name"));

		//set editable using textfield and built in integer conversion
		percentCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		taskCol.setCellFactory(TextFieldTableCell.<Task>forTableColumn());

		//edit listener to execute custom mysql updating code
		taskCol.setOnEditCommit(e -> {
			c.runSQL("update " + tableName + " set name = \'" + e.getNewValue().replaceAll("'", "''") + "\' where id = " + e.getRowValue().getId());
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
				c.runSQL("update " + tableName + " set progress = \'" + val + "\' where id = " + e.getRowValue().getId());
			}
		});

		tableview.setItems(list);
	}

	void loadData() {
		list.clear();
		c.runSQL("select * from " + tableName + ";");		
		//int id, String name, int menuID, int progress, String link
		try {
			while(c.rs.next()) {
				list.add(new Task(c.rs.getInt("id"),c.rs.getString("name"), c.rs.getInt("menuID"), c.rs.getInt("progress"), c.rs.getString("link")));
			}
		} catch (SQLException e1) {e1.printStackTrace();}
	}

	Boolean addEntry(String name, int menuID) {
		c.runSQL(String.format("insert into %s (name, menuID, progress) values ('%s','%d','0');", tableName, name.replaceAll("'", "''"), menuID));	
		c.runSQL(String.format("select * from %s order by id desc limit 1", tableName));	//get id of the last added row
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
			//puts user in edit mode for newly added (last) row's name
			int lastRowIndex = tableview.getItems().size() - 1;	
			tableview.edit(lastRowIndex, taskCol);
			tableview.scrollTo(list.get(lastRowIndex)); //causes buggy behavior
		}
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
