package application.java;

import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
public class MenuController {
	@FXML 
	Text source;
	@FXML 
	Text target;
	@FXML
	ListView<String> listview;
	String tableName;
	Connect c;
	DoubleProperty widthProperty, heightProperty;
	ObservableList<SimpleEntry<Integer,String>> items;
	
	@FXML
	private void initialize() {
		/*items =FXCollections.observableArrayList (
			    "Placeholder", "Menu", "Leetcode", "Piano");
		listview.setItems(items);*/
	}
    @FXML
    void drag(MouseEvent event) {
    	/*
    	Node source = ((Node) event.getSource());
        Dragboard db = source.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putString(((Node) event.getSource()).getText());
        db.setContent(content);
        event.consume();*/

    	String source = listview.getSelectionModel().getSelectedItem();
        Dragboard db = listview.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putString(source);
        db.setContent(content);
        event.consume();
        //String selected = listview.getSelectionModel().getSelectedItem();
        
    }
    
    void setWidthProperty(ReadOnlyDoubleProperty widthProperty) {
    	listview.prefWidthProperty().bind(widthProperty);
    }
    void setHeightProperty(ReadOnlyDoubleProperty heightProperty) {
    	listview.prefHeightProperty().bind(heightProperty.multiply(0.3));
    }
    
    void setConnect(Connect c) {
    	this.c = c;
    	
    }
    
    void loadData() { //figure out how to load 2d array into a listview??? 
    	items.clear();
		c.runSQL("select * from " + tableName + ";");		
		//int id, String name, int menuID, int progress, String link
		try {
			while(c.rs.next()) {
				items.add(new SimpleEntry<Integer,String>(c.rs.getInt("id"),c.rs.getString("name")));
			}
			listview.setCellFactory(param -> new ListCell<String>() {
			    @Override
			    protected void updateItem(Word item, boolean empty) {
			        super.updateItem(item, empty);

			        if (empty || item == null || item.getWord() == null) {
			            setText(null);
			        } else {
			            setText(item.getWord());
			        }
			    }
			});
		} catch (SQLException e1) {e1.printStackTrace();}
    }


}
