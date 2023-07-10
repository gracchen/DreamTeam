package application.java;
	

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class Main extends Application {
	
	private Controller controller;
	
	public void start(Stage primaryStage) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/resources/main-view.fxml"));
		Parent root = null;
		try {
			root = (Parent)fxmlLoader.load();
		} catch (IOException e) {e.printStackTrace();}
		controller = fxmlLoader.<Controller>getController();
		controller.setMain(this);
		primaryStage.setTitle("Dream Team Go!");
		Scene scene = new Scene(root, 1200, 700);
		//scene.addEventFilter(MouseEvent.ANY, e -> System.out.println( e));
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setMinWidth(630);	//prevent weird formatting if user resizes too small
		primaryStage.setMinHeight(400);
		
		root.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
			controller.mouseClick();
		});
	}
	
	public void openLink(String link) {
	    System.out.println("Opening " + link);
	    
	}
	
	public static void main(String[] args) {		
		launch(args);
	}


}
