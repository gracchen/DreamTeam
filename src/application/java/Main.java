package application.java;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {
	public void start(Stage primaryStage) {
		
		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("/application/resources/main-view.fxml"));
		} catch (IOException e) {e.printStackTrace();}
		primaryStage.setTitle("Dream Team Go Go!");
		primaryStage.setScene(new Scene(root, 1200, 700));
		primaryStage.show();
		primaryStage.setMinWidth(630);	//prevent weird formatting if user resizes too small
		primaryStage.setMinHeight(400);
	}
	
	public static void main(String[] args) {		
		launch(args);
	}
}
