module DreamTeam {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;
	requires java.sql;
	requires java.desktop;
	
	opens application.java to javafx.graphics, javafx.fxml, javafx.base, javafx.sql, javafx.controls;
}
