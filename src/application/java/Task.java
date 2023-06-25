package application.java;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Task {
    private IntegerProperty id;
    private StringProperty name;
    private IntegerProperty menuID;
    private IntegerProperty progress;
    private StringProperty link;

    public Task(int id, String name, int menuID, int progress, String link) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.menuID = new SimpleIntegerProperty(menuID);
        this.progress = new SimpleIntegerProperty(progress);
        this.link = new SimpleStringProperty(link);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public int getMenuID() {
        return menuID.get();
    }

    public void setMenuID(int menuID) {
        this.menuID.set(menuID);
    }

    public IntegerProperty menuIDProperty() {
        return menuID;
    }

    public int getProgress() {
        return progress.get();
    }

    public void setProgress(int progress) {
        this.progress.set(progress);
    }

    public IntegerProperty progressProperty() {
        return progress;
    }

    public String getLink() {
        return link.get();
    }

    public void setLink(String link) {
        this.link.set(link);
    }

    public StringProperty linkProperty() {
        return link;
    }
}





