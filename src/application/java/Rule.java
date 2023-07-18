package application.java;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Rule {
    private SimpleLongProperty id;
    private IntegerProperty menuID;
    private StringProperty name;
    private BooleanProperty mon;
    private BooleanProperty tues;
    private BooleanProperty wed;
    private BooleanProperty thurs;
    private BooleanProperty fri;
    private BooleanProperty sat;
    private BooleanProperty sun;
    @SuppressWarnings("unused")
	private Connect c;
    
    public Rule(long id, String name, int menuID, Boolean mon,
    		Boolean tues, Boolean wed, Boolean thurs, Boolean fri, Boolean sat,
    		Boolean sun, Connect c) {
        super();
        this.id = new SimpleLongProperty(id);
        this.menuID = new SimpleIntegerProperty(menuID);
        this.name = new SimpleStringProperty(name);
        this.mon = new SimpleBooleanProperty(mon);
        this.tues = new SimpleBooleanProperty(tues);
        this.wed = new SimpleBooleanProperty(wed);;
        this.thurs = new SimpleBooleanProperty(thurs);
        this.fri = new SimpleBooleanProperty(fri);
        this.sat = new SimpleBooleanProperty(sat);
        this.sun = new SimpleBooleanProperty(sun);
        this.c = c;

        
        this.mon.addListener((observable, oldValue, newValue) -> {
			this.mon.set(newValue); //flip value locally
			c.runSQL("update Rules set mon = " + ( newValue ? 1 : 0 )+ " where id = " + id); //and remotely
		});
        this.tues.addListener((observable, oldValue, newValue) -> {
			this.tues.set(newValue); //flip value locally
			c.runSQL("update Rules set tues = " + ( newValue ? 1 : 0 )+ " where id = " + id); //and remotely
		});
        this.wed.addListener((observable, oldValue, newValue) -> {
			this.wed.set(newValue); //flip value locally
			c.runSQL("update Rules set wed = " + ( newValue ? 1 : 0 )+ " where id = " + id); //and remotely
		});
        this.thurs.addListener((observable, oldValue, newValue) -> {
			this.thurs.set(newValue); //flip value locally
			c.runSQL("update Rules set thurs = " + ( newValue ? 1 : 0 )+ " where id = " + id); //and remotely
		});
        this.fri.addListener((observable, oldValue, newValue) -> {
			this.fri.set(newValue); //flip value locally
			c.runSQL("update Rules set fri = " + ( newValue ? 1 : 0 ) + " where id = " + id); //and remotely
		});
        this.sat.addListener((observable, oldValue, newValue) -> {
			this.sat.set(newValue); //flip value locally
			c.runSQL("update Rules set sat = " + ( newValue ? 1 : 0 ) + " where id = " + id); //and remotely
		});
        this.sun.addListener((observable, oldValue, newValue) -> {
			this.sun.set(newValue); //flip value locally
			c.runSQL("update Rules set sun = " + ( newValue ? 1 : 0 ) + " where id = " + id); //and remotely
		});
		
    }

    // id property
    public SimpleLongProperty idProperty() {
        return id;
    }

    public long getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    // menuID property
    public IntegerProperty menuIDProperty() {
        return menuID;
    }

    public int getMenuID() {
        return menuID.get();
    }

    public void setMenuID(int menuID) {
        this.menuID.set(menuID);
    }

    // name property
    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    // mon property
    public BooleanProperty monProperty() {
        return mon;
    }

    public boolean isMon() {
        return mon.get();
    }

    public void setMon(boolean mon) {
        this.mon.set(mon);
    }

    // tues property
    public BooleanProperty tuesProperty() {
        return tues;
    }

    public boolean isTues() {
        return tues.get();
    }

    public void setTues(boolean tues) {
        this.tues.set(tues);
    }

    // wed property
    public BooleanProperty wedProperty() {
        return wed;
    }

    public boolean isWed() {
        return wed.get();
    }

    public void setWed(boolean wed) {
        this.wed.set(wed);
    }

    // thurs property
    public BooleanProperty thursProperty() {
        return thurs;
    }

    public boolean isThurs() {
        return thurs.get();
    }

    public void setThurs(boolean thurs) {
        this.thurs.set(thurs);
    }

    // fri property
    public BooleanProperty friProperty() {
        return fri;
    }

    public boolean isFri() {
        return fri.get();
    }

    public void setFri(boolean fri) {
        this.fri.set(fri);
    }

    // sat property
    public BooleanProperty satProperty() {
        return sat;
    }

    public boolean isSat() {
        return sat.get();
    }

    public void setSat(boolean sat) {
        this.sat.set(sat);
    }

    // sun property
    public BooleanProperty sunProperty() {
        return sun;
    }

    public boolean isSun() {
        return sun.get();
    }

    public void setSun(boolean sun) {
        this.sun.set(sun);
    }
}
