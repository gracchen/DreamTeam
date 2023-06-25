package application.java;

import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

public class MenuController {
	@FXML 
	BorderPane view;
	
	Controller main;

	@FXML
	ListView<SimpleEntry<Integer,String>> listview;
	String tableName = "menu";
	Connect c;
	DoubleProperty widthProperty, heightProperty;
	ObservableList<SimpleEntry<Integer,String>> items = FXCollections.observableArrayList();
	Boolean mouseInMenu = false;
	
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
    	listview.getSelectionModel().clearSelection();
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
    	listview.getSelectionModel().select(listview.getSelectionModel().getSelectedIndex()); //select next item automatically
    }
    
    Boolean addEntry(String name) {
    	c.runSQL(String.format("insert into %s (name) values ('%s');", tableName, name.replace("'", "''")));	
    	c.runSQL(String.format("select * from %s order by id desc limit 1", tableName));	//get id of the last added row
		try {
			if (c.rs.next()) {
				items.add(new SimpleEntry<Integer, String>(c.rs.getInt("id"), name));
				listview.scrollTo(items.get(items.size()-1));
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
    
    void setMain(Controller m) {
    	main = m;
    	listview.getSelectionModel().selectedItemProperty().addListener(e -> {
    		//System.out.println("selection changed!");
    		if (main != null & listview.getSelectionModel().getSelectedIndex() != -1) {
    			main.highlightMenuID(listview.getSelectionModel().getSelectedItem().getKey());
    		}
    		
    	});
    }

    Boolean getMouseInMenu() {
    	return mouseInMenu;
    }
    
	@FXML
	void enterList(MouseEvent event) {
		mouseInMenu = true;
	}	
	@FXML
	void exitList(MouseEvent event) {
		mouseInMenu = false;
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
		            textField.requestFocus();
			    }
			    
			    @Override
			    public void cancelEdit() {
			        super.cancelEdit();
			        if (getItem() != null) setText(getItem().getValue());
			        setGraphic(null);
			        setContentDisplay(ContentDisplay.TEXT_ONLY);
			    }

			    @Override
			    public void commitEdit(SimpleEntry<Integer, String> newValue) {
			    	//newValue.setValue(newValue.getValue().replace("'","''"));
			        super.commitEdit(newValue);
			        if (newValue != null) setText(newValue.getValue());
			        setGraphic(null);
			        setContentDisplay(ContentDisplay.TEXT_ONLY);
			        System.out.println("commited edit");
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
	            items.set(event.getIndex(), editedEntry);

				String string = editedEntry.getValue();
				String n = string.replaceAll("'", "''");
				System.out.println(String.format("replacing name \"%s\" to name \"%s\"",string, n));
	            c.runSQL("update " + tableName + " set name = \'" + n + "\' where id = " + editedEntry.getKey());
		        System.out.println("on edit commit");
		        main.editMenu(editedEntry.getKey(), string);
	        });
		} catch (SQLException e1) {e1.printStackTrace();}
    }


}
