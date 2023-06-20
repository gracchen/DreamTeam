package application.java;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

public class DayController {
    @FXML
    private Label header;
	private StringProperty tableName;
	@FXML
	private Button addButton;
	@FXML
	private Button delButton;
    @FXML
    private TableColumn<?, String> taskCol;
    @FXML
    private TableColumn<?, Integer> percentCol;
    @FXML
    private TableView<?> tableview;
    
    @FXML
	private void initialize() {
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
	}
	
    @FXML
    void addClicked(ActionEvent event) {
    	System.out.println("Add clicked! My name is " + tableName.get());
    }
    
    @FXML
    void delClicked(ActionEvent event) {
    	System.out.println("Delete clicked! My name is " + tableName.get());
    }
    
    void setTableName(String name) {
    	tableName.set(name); //handle for main controller to tell me what table to pull from
    }

}
