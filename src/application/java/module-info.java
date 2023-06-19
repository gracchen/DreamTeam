module DreamTeam {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	
	opens application.java to javafx.graphics, javafx.fxml;
	opens application.resources to javafx.graphics, javafx.fxml;
}
