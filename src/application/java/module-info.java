module DreamTeam {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;
	
	opens application.java to javafx.graphics, javafx.fxml;
}
