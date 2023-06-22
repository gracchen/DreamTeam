package application.java;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class Controller {
	private Connect c;
	String[] weekdays = {"Mon", "Tues", "Wed", "Thurs", "Fri", "Sat", "Sun"};
	DayController[] controllers = new DayController[7];
	
	@FXML
	private HBox calendar;
	//@FXML
	//private ListView<String> menu;
	
	@FXML
	private BorderPane stage;
	@FXML
	private FlowPane menu;
	
	@FXML
	private void initialize() {
		System.out.println("initializing....");
		//initialize connection to sql database:
		c = new Connect(); //initialize connection w/ database
		
		//add 7 instances of pane-1
		LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        int dayOfWeekValue = (dayOfWeek.getValue() - 1) % 7;
        
		for (int i = 0; i < 7; i++) {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/resources/day.fxml"));
			Parent root = null;
			try {
				root = (Parent)fxmlLoader.load();
			} catch (IOException e) {e.printStackTrace();}          
			controllers[i] = fxmlLoader.<DayController>getController();
			controllers[i].setTableName(weekdays[i]);	//names the fxmls so they know which table in charge of
			controllers[i].setConnect(c);	//gives each day access to a shared sql connection
			calendar.getChildren().add(root);
			HBox.setHgrow(root, Priority.ALWAYS); //allow tableview to grow if greater than pref dimensions
			if (dayOfWeekValue == i) {
				controllers[i].highlight();
			}
		}
		ObservableList<String> items =FXCollections.observableArrayList (
			    "Placeholder", "Menu", "Leetcode", "Piano");
		//menu.setItems(items);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/resources/menu.fxml"));
		Parent root2 = null;
		try {
			root2 = (Parent)fxmlLoader.load();
		} catch (IOException e) {e.printStackTrace();}          
		MenuController ctrl = fxmlLoader.<MenuController>getController();
		ctrl.setHeightProperty(stage.heightProperty());
		ctrl.setWidthProperty(stage.widthProperty());
		ctrl.setConnect(c);
		menu.getChildren().add(root2);
		//mouse clicker that sends signal to all tables to deselect if needed
		
	}

    void mouseClick() {
    	Boolean mouseInADayPane = false;
    	for (int i = 0; i < 7 && !mouseInADayPane; i++) {
    		mouseInADayPane = !controllers[i].getMouseOut();
    	}
    	if (!mouseInADayPane) {
    		for (int i = 0; i < 7; i++) {
    			controllers[i].deselect();
    		}
    	}
    	//System.out.println("clicked!");
    }
	
	@FXML
	public void handleButtonClick() {
		System.out.println("buttonClick");
	}
}
