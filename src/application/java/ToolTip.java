package application.java;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.control.Tooltip;
import javafx.stage.Window;
import javafx.util.Duration;

public class ToolTip extends Tooltip {

    private BooleanProperty isHoveringProperty = new ReadOnlyBooleanWrapper(false);

    public ToolTip(String text) {
        super(text);
        setAutoHide(false);
        setHideOnEscape(false);
        setShowDelay(Duration.millis(100));
    }

    public boolean isHovering() {
        return isHoveringProperty.get();
    }

    public BooleanProperty isHoveringProperty() {
        return isHoveringProperty;
    }

    @Override
    public void hide() {
        if (!isHovering()) {
            super.hide();
        }
    }

    @Override
    public void show(Window ownerWindow, double screenX, double screenY) {
        super.show(ownerWindow, screenX, screenY);
        // Register event handlers for the tooltip
        getScene().getWindow().getScene().setOnMouseEntered(e -> {
            isHoveringProperty.set(true);
            super.show(ownerWindow, screenX, screenY);
        });
        getScene().getWindow().getScene().setOnMouseExited(e -> {
            isHoveringProperty.set(false);
            super.hide();
        });
    }
}
