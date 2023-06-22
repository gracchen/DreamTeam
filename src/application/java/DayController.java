package application.java;

import java.sql.SQLException;

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
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
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
	private StringProperty tableName;
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
		
		//bind header to name of table
		tableName = new SimpleStringProperty();
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
    
    void initTable() {		
    	//set data to display
    	percentCol.setCellValueFactory(new PropertyValueFactory<Task,Integer>("progress"));
    	taskCol.setCellValueFactory(new PropertyValueFactory<Task,String>("name"));

    	//set editable using textfield and built in integer conversion
    	percentCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
    	taskCol.setCellFactory(TextFieldTableCell.<Task>forTableColumn());

    	//edit listener to execute custom mysql updating code
    	taskCol.setOnEditCommit(e -> {
    		c.runSQL("update " + tableName.get() + " set name = \"" + e.getNewValue() + "\" where id = " + e.getRowValue().getId());
    	});
    	percentCol.setOnEditCommit(e -> {
    		int val = e.getNewValue();
    		
    		if (val < 0 || val > 100) {	//% out of range
    			e.getRowValue().setProgress(e.getOldValue()); //don't touch sql, undo user's change
    			loadData();
    		}
	    	else { //commit to sql
	    		c.runSQL("update " + tableName.get() + " set progress = \"" + val + "\" where id = " + e.getRowValue().getId());
    		}
    	});
    	
    	tableview.setItems(list);
    	//allow multi selection for quicker delete
    	tableview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    
    @FXML
    public void dragOver(DragEvent event) {
        if (event.getGestureSource() != tableview && event.getDragboard().hasString())
            event.acceptTransferModes(TransferMode.COPY);
        event.consume();
    }
    
    @FXML
    public void drop(DragEvent event) { 
        Dragboard db = event.getDragboard();
        boolean success = db.hasString();
        if (db.hasString()) {
        	System.out.println(db.getString());
        	addEntry(db.getString());
        }
        event.setDropCompleted(success);
        event.consume();
    }
    
    void loadData() {
    	list.clear();
		c.runSQL("select * from " + tableName.get() + ";");		
		//int id, String name, int menuID, int progress, String link
		try {
			while(c.rs.next()) {
				list.add(new Task(c.rs.getInt("id"),c.rs.getString("name"), c.rs.getInt("menuID"), c.rs.getInt("progress"), c.rs.getString("link")));
			}
		} catch (SQLException e1) {e1.printStackTrace();}
    }
    
    Boolean addEntry(String name) {
    	c.runSQL(String.format("insert into %s (name, menuID, progress) values ('%s','-1','0');", tableName.get(), name));	
    	c.runSQL(String.format("select * from %s order by id desc limit 1", tableName.get()));	//get id of the last added row
		try {
			if (c.rs.next()) {
				list.add(new Task(c.rs.getInt("id"),c.rs.getString("name"), c.rs.getInt("menuID"), c.rs.getInt("progress"), c.rs.getString("link")));
				
				return true;

			}
		} catch (SQLException e1) {e1.printStackTrace();}
		return false;
    }
    
    @FXML
    void addClicked(ActionEvent event) {
    	if (addEntry("")) { //if adding successful
    		//puts user in edit mode for newly added (last) row's name
            int lastRowIndex = tableview.getItems().size() - 1;	
            tableview.edit(lastRowIndex, taskCol);
    	}
    }
    
    @FXML
    void delClicked(ActionEvent event) {
    	ObservableList<Task> selectedItems = tableview.getSelectionModel().getSelectedItems();

    	for (Task task : selectedItems) {
    	    c.runSQL(String.format("delete from %s where id=%d", tableName.get(), task.getId()));
    	}
    	
    	list.removeAll(selectedItems);
    }   
    
    @FXML
    void enterDayPane(MouseEvent event) {
    	mouseOut = false;
    }
    
    void deselect() {
    	tableview.getSelectionModel().clearSelection();
    }
    
    @FXML
    void exitDayPane(MouseEvent event) {
    	mouseOut = true;
    }
    
    Boolean getMouseOut() {
    	return mouseOut;
    }
    
    void setTableName(String name) {
    	tableName.set(name); //handle for main controller to tell me what table to pull from
    }
    
    void setConnect(Connect c) {
    	this.c = c;
    	loadData();
    }

}
