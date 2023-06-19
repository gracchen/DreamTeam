package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class Pane1Controller {
	@FXML
	Button butt;
    @FXML
    void buttonClicked(ActionEvent event) {
    	System.out.println("Button clicked!");
    }
    
    void setButton(String text) {
    	butt.setText(text);
    }

}
