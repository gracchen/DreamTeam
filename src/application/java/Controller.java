package application.java;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.ButtonBar;

public class Controller {
	private Connect c;
	String[] weekdays = {"Mon", "Tues", "Wed", "Thurs", "Fri", "Sat", "Sun"};
	DayController[] controllers = new DayController[7];

	@FXML private HBox calendar;
	@FXML private SplitPane split;
	@FXML private BorderPane stage;
	@FXML private Parent menu;
	@FXML private MenuController menuController;
	@FXML private Parent rule;
	@FXML private RuleController ruleController;
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

		menuController.setMain(this);
		menuController.setConnect(c);
		
		//ruleController.setMain(this);
		ruleController.setConnect(c);
	}

	public void highlightMenuID(int menuID) {
		System.out.println("highlightMenuID(" + menuID + ");");
		for (int i = 0; i < 7; i++) {
			controllers[i].highlight(menuID);
		}
	}
	public void editMenu(int menuID, String newVal) {
		
		ButtonType yes = new ButtonType("yes", ButtonBar.ButtonData.OK_DONE);
		ButtonType no = new ButtonType("no", ButtonBar.ButtonData.CANCEL_CLOSE);
		Alert alert = new Alert(AlertType.NONE, "Do you want the rename to apply to all instances within the week?", yes, no);
		alert.setTitle("Rename all?");

		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == yes) {
			System.out.println("editMenuID(" + menuID + ", "+ newVal + ");");
			for (int i = 0; i < 7; i++) {
				controllers[i].editMenu(menuID,newVal);
			}
		}
	}
	
	public void deleteMenuID(int menuID) {
		for (int i = 0; i < 7; i++) {
			controllers[i].deleteMenuID(menuID);
		}
	}
	
    void mouseClick() {
    	Boolean mouseInADayPane = false;
    	for (int i = 0; i < 7 && !mouseInADayPane; i++) {
    		mouseInADayPane = !controllers[i].getMouseOut();
    	}
    	if (menuController.getMouseInMenu() == false) {
        	if (!mouseInADayPane) {
        		//System.out.println("deselect time :D");
        		for (int i = 0; i < 7; i++) {
        			controllers[i].deselect();
        		}
        		menuController.deselect();
        	}
    	}

    	//System.out.println("clicked!");
    }
	
	@FXML
	public void handleButtonClick() {
		System.out.println("buttonClick");
	}
}
