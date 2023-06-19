package application;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class Controller {

	int i = 0;
	@FXML
	private GridPane calendar;
	
	@FXML
	private void initialize() {
		System.out.println("initializing....");
		calendar.add(new Label("hi"), i, 0);
	}
	@FXML
	public void handleButtonClick() {
		System.out.println("Creating new instance of pane-1");
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("resources/pane-1.fxml"));
			Parent root = (Parent)fxmlLoader.load();          
			Pane1Controller controller = fxmlLoader.<Pane1Controller>getController();
			controller.setButton(String.valueOf(i));
			calendar.add(root, i, 1);
			//calendar.add(FXMLLoader.load(getClass().getResource("pane-1.fxml")), i, 1);
		} catch (IOException e) {e.printStackTrace();}
		//calendar.add(new Label("hi"), i, 1);
		i++;
	}
}
