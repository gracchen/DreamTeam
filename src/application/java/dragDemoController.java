package application.java;

import javafx.fxml.FXML;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;

public class dragDemoController {
	@FXML 
	Text source;
	@FXML 
	Text target;
    @FXML
    void drag(MouseEvent event) {
    	Dragboard db = source.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putString(source.getText());
        db.setContent(content);
        event.consume();
    }
    
    @FXML
    public void dragOver(DragEvent event) {
        if (event.getGestureSource() != target && event.getDragboard().hasString())
            event.acceptTransferModes(TransferMode.COPY);
        event.consume();
    }
    
    @FXML
    public void drop(DragEvent event) { 
        Dragboard db = event.getDragboard();
        boolean success = db.hasString();
        if (db.hasString()) target.setText(db.getString());
        event.setDropCompleted(success);
        event.consume();
    }


}
