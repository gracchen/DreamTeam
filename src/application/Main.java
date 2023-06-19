package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application {

	public void start(Stage primaryStage) {
		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("resources/main-view.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		primaryStage.setTitle("Hello World");
		primaryStage.setScene(new Scene(root, 500, 275));
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
