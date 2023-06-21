package application.java;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import application.java.Connect;

public class Controller {
	private Connect c;
	String[] weekdays = {"Mon", "Tues", "Wed", "Thurs", "Fri", "Sat", "Sun"};
	DayController[] controllers = new DayController[7];
	
	@FXML
	private HBox calendar;
	@FXML
	private ListView<String> menu;
	@FXML
	private void initialize() {
		System.out.println("initializing....");
		//initialize connection to sql database:
		c = new Connect(); //initialize connection w/ database
		
		//add 7 instances of pane-1
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
		}
		ObservableList<String> items =FXCollections.observableArrayList (
			    "Single", "Double", "Suite", "Family App");
		menu.setItems(items);
	}
	
	@FXML
	public void handleButtonClick() {
		System.out.println("buttonClick");
	}
}
