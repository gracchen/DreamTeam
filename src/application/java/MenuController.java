package application.java;

import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

public class MenuController {
	@FXML 
	BorderPane view;

	@FXML
	ListView<SimpleEntry<Integer,String>> listview;
	String tableName = "menu";
	Connect c;
	DoubleProperty widthProperty, heightProperty;
	ObservableList<SimpleEntry<Integer,String>> items = FXCollections.observableArrayList();
	
    @FXML
    void drag(MouseEvent event) {
    	SimpleEntry<Integer, String> source = listview.getSelectionModel().getSelectedItem();
        Dragboard db = listview.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putString(source.getKey() + ":"  + source.getValue());
        db.setContent(content);
        event.consume();
    }
    
    @FXML
    void addClick(ActionEvent event) {
    	System.out.println("menu add clicked");
    	if (addEntry("")) { //if adding successful
    		//puts user in edit mode for newly added (last) row's name
            int lastRowIndex = listview.getItems().size() - 1;	
            listview.edit(lastRowIndex);
    	}
    }
    @FXML
    void delClick(ActionEvent event) {
    	System.out.println("menu delete clicked");
    	SimpleEntry<Integer,String> selectedItem = listview.getSelectionModel().getSelectedItem();

    	c.runSQL(String.format("delete from %s where id=%d", tableName, selectedItem.getKey()));
    	
    	items.remove(selectedItem);
    }
    
    Boolean addEntry(String name) {
    	c.runSQL(String.format("insert into %s (name) values ('%s');", tableName, name));	
    	c.runSQL(String.format("select * from %s order by id desc limit 1", tableName));	//get id of the last added row
		try {
			if (c.rs.next()) {
				items.add(new SimpleEntry<Integer, String>(c.rs.getInt("id"),c.rs.getString("name")));
				return true;
			}
		} catch (SQLException e1) {e1.printStackTrace();}
		return false;
    }
    
    void setWidthProperty(ReadOnlyDoubleProperty widthProperty) {
    	view.prefWidthProperty().bind(widthProperty);
    }
    void setHeightProperty(ReadOnlyDoubleProperty heightProperty) {
    	view.prefHeightProperty().bind(heightProperty.multiply(0.3));
    }
    
    void setConnect(Connect c) {
    	this.c = c;
    	loadData();
    }
    
    void loadData() { //figure out how to load 2d array into a listview??? 
    	items.clear();
		c.runSQL("select * from " + tableName + ";");		
		//int id, String name, int menuID, int progress, String link
		try {
			while(c.rs.next()) {
				int i = c.rs.getInt("id");
				String n = c.rs.getString("name");
				items.add(new SimpleEntry<Integer,String>(i,n));
				//items.add(new SimpleEntry<Integer,String>(c.rs.getInt("id"),c.rs.getString("name")));
				System.out.print(i + " : " + n);
			}
			listview.setItems(items);
			listview.setEditable(true);

			listview.setCellFactory(param -> new ListCell<SimpleEntry<Integer,String>>() {
				private TextField textField;
			    @Override
			    protected void updateItem(SimpleEntry<Integer,String> item, boolean empty) {
			        super.updateItem(item, empty);
			        if (empty || item == null || item == null) {
			            setText(null);
			        } else {
			            setText(item.getValue());
			        }
			        setGraphic(null);
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
			        textField.setText(getItem().getValue()); // Set initial text
			        setText(null);
			        setGraphic(textField);
			        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			        //textField.selectAll();
		            Platform.runLater(() -> textField.requestFocus());
			        //textField.setVisible(true);
			    }
			    
			    @Override
			    public void cancelEdit() {
			        super.cancelEdit();
			        setText(getItem().getValue());
			        setGraphic(null);
			        setContentDisplay(ContentDisplay.TEXT_ONLY);
			        //System.out.println("Canceled");
			    }

			    @Override
			    public void commitEdit(SimpleEntry<Integer, String> newValue) {
			        super.commitEdit(newValue);
			        setText(newValue.getValue());
			        setGraphic(null);
			        setContentDisplay(ContentDisplay.TEXT_ONLY);
			        System.out.println("commited edit");
			        //textField.setVisible(false);
			    }

			    private void createTextField() {
			        textField = new TextField(getItem().getValue());
			        textField.setOnKeyPressed(event -> {
			            if (event.getCode() == KeyCode.ENTER) {
			                commitEdit(new SimpleEntry<>(getItem().getKey(), textField.getText()));
					        System.out.println("Enter pressed");
					        event.consume();
			            }
			        });
			    }
			});
			
			listview.setOnEditCommit(event -> {
	            SimpleEntry<Integer, String> editedEntry = event.getNewValue();
	            int index = event.getIndex();
	            items.set(index, editedEntry);
	            c.runSQL("update " + tableName + " set name = \"" + editedEntry.getValue() + "\" where id = " + editedEntry.getKey());
		        System.out.println("on edit commit");
	        });
		} catch (SQLException e1) {e1.printStackTrace();}
    }


}
