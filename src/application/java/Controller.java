package application.java;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class Controller {
	private Connect c;
	String[] weekdays = { "Mon", "Tues", "Wed", "Thurs", "Fri", "Sat", "Sun" };

	@FXML
	private HBox calendars;
	@FXML
	private HBox week;
	@FXML
	private SplitPane split;
	@FXML
	private BorderPane stage;
	@FXML
	private VBox dayRows;
	@FXML
	private Parent menu;
	@FXML
	private MenuController menuController;
	@FXML
	private Parent rule;
	@FXML
	private RuleController ruleController;
	@FXML
	private Parent m, t, w, r, f, sa, su;
	@FXML
	private Parent m2, t2, w2, r2, f2, sa2, su2;
	@FXML
	private DayController mController, tController, wController, rController, fController, saController, suController;
	@FXML
	private DayController m2Controller, t2Controller, w2Controller, r2Controller, f2Controller, sa2Controller,
	su2Controller;
	@FXML private CalendarController calendarController;
	@FXML private Parent calendar;
	DayController[] controllers = new DayController[7];
	DayController[] calControllers = new DayController[7];
	Parent[] roots = new Parent[7];
	Parent[] calRoots = new Parent[7];
	LocalDate calDay,thisWeek;
	@FXML
	private void initialize() {
		System.out.println("initializing....");
		//initialize connection to sql database:
		c = new Connect(); //initialize connection w/ database

		//add 7 instances of pane-1
		LocalDate today = LocalDate.now();
		DayOfWeek dayOfWeek = today.getDayOfWeek();
		int dayOfWeekValue = (dayOfWeek.getValue() - 1) % 7;
		dayRows.getChildren().remove(calendars);

		VBox.setVgrow(week, Priority.ALWAYS);
		DayController[] ct = { mController, tController, wController, rController, fController, saController,
				suController };
		System.arraycopy(ct, 0, controllers, 0, 7);
		Parent[] ro = { m, t, w, r, f, sa, su };
		System.arraycopy(ro, 0, roots, 0, 7);

		DayController[] ct2 = { m2Controller, t2Controller, w2Controller, r2Controller, f2Controller, sa2Controller,
				su2Controller };
		System.arraycopy(ct2, 0, calControllers, 0, 7);
		Parent[] ro2 = { m2, t2, w2, r2, f2, sa2, su2 };
		System.arraycopy(ro2, 0, calRoots, 0, 7);

		thisWeek = LocalDate.now();
		while (thisWeek.getDayOfWeek() != DayOfWeek.MONDAY) {
			thisWeek = thisWeek.minusDays(1);
		}

		for (int i = 0; i < 7; i++) {
			controllers[i].initializeVals(weekdays[i], thisWeek.plusDays(i), c);
			//controllers[i].setTableName(weekdays[i]);	//names the fxmls so they know which table in charge of
			//controllers[i].setConnect(c);	//gives each day access to a shared sql connection
			HBox.setHgrow(roots[i], Priority.ALWAYS); //allow tableview to grow if greater than pref dimensions
			if (dayOfWeekValue == i) {
				controllers[i].highlight();
			}
		}

		menuController.setMain(this);
		menuController.setConnect(c);
		calendarController.setMain(this);
		//ruleController.setMain(this);
		ruleController.setConnect(c);
	}

	public void showCalendarWeek(LocalDate a) {
		System.out.println(a);
		if (thisWeek.equals(a)) {
			hideCalendarWeek();
			return;
		}
		if (!dayRows.getChildren().contains(calendars)) {
			dayRows.getChildren().add(calendars);
			calDay = a;
		}
		else {
			if (calDay.equals(a)) return; //same week do nothing
		}
		for (int i = 0; i < 7; i++) {
			calControllers[i].initializeVals(weekdays[i], a.plusDays(i),c);
			HBox.setHgrow(calRoots[i], Priority.ALWAYS); //allow tableview to grow if greater than pref dimensions
		}
	}

	public void hideCalendarWeek() {
		dayRows.getChildren().remove(calendars);
		calDay = null;
	}

	public void setMain(Main m) {
		for (int i = 0; i < 7; i++) {
			controllers[i].setMain(m);
		}
		for (int i = 0; i < 7; i++) {
			calControllers[i].setMain(m);
		}
	}

	@FXML
	public void bigReset(ActionEvent e) {

		/*		for (int i = 0; i < 7; i++) {
					c.runSQL("truncate table " + weekdays[i] + ";");
					controllers[i].reset();
				}
				c.runSQL("select * from Rules;");
				List<Rule> a = new LinkedList<Rule>();
				try {
					while(c.rs.next()) {
						a.add(new Rule(c.rs.getInt("id"),c.rs.getString("name"),c.rs.getInt("menuID"),  c.rs.getBoolean("mon"), c.rs.getBoolean("tues"), 
								c.rs.getBoolean("wed"), c.rs.getBoolean("thurs"), c.rs.getBoolean("fri"), c.rs.getBoolean("sat"), c.rs.getBoolean("sun"), c));
					}
				} catch (SQLException e1) {e1.printStackTrace();}
				for (Rule r : a) {
		    if (r.isMon()) controllers[0].addEntry(r.getName(), r.getMenuID());
		    if (r.isTues()) controllers[1].addEntry(r.getName(), r.getMenuID());
		    if (r.isWed()) controllers[2].addEntry(r.getName(), r.getMenuID());
		    if (r.isThurs()) controllers[3].addEntry(r.getName(), r.getMenuID());
		    if (r.isFri()) controllers[4].addEntry(r.getName(), r.getMenuID());
		    if (r.isSat()) controllers[5].addEntry(r.getName(), r.getMenuID());
		    if (r.isSun()) controllers[6].addEntry(r.getName(), r.getMenuID());
				}*/
	}

	public void highlightMenuID(int menuID) {
		System.out.println("highlightMenuID(" + menuID + ");");
		for (int i = 0; i < 7; i++) {
			controllers[i].highlight(menuID);
		}
		ruleController.highlight(menuID);
	}

	public void editMenu(int menuID, String newVal) {
		ruleController.updateEntry(menuID, newVal);
		Boolean doAlert = false;
		for (int i = 0; i < 7 && !doAlert; i++) {
			if (controllers[i].hasTask(menuID))
				doAlert = true;
		}
		if (!doAlert)
			return; //do nothing
		//otherwise ask
		ButtonType yes = new ButtonType("yes", ButtonBar.ButtonData.OK_DONE);
		ButtonType no = new ButtonType("no", ButtonBar.ButtonData.CANCEL_CLOSE);
		Alert alert = new Alert(AlertType.NONE, "Do you want the rename to apply to all instances within the week?",
				yes, no);
		alert.setTitle("Rename all?");

		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == yes) {
			System.out.println("editMenuID(" + menuID + ", " + newVal + ");");
			for (int i = 0; i < 7; i++) {
				controllers[i].editMenu(menuID, newVal);
			}
		}
	}

	public void deleteMenuID(int menuID) {

		Boolean doAlert = false;
		for (int i = 0; i < 7 && !doAlert; i++) {
			if (controllers[i].hasTask(menuID))
				doAlert = true;
		}
		if (!doAlert)
			return; //do nothing

		ButtonType yes = new ButtonType("yes", ButtonBar.ButtonData.OK_DONE);
		ButtonType no = new ButtonType("no", ButtonBar.ButtonData.CANCEL_CLOSE);
		Alert alert = new Alert(AlertType.NONE, "Do you want to also delete all instances within the week?", yes, no);
		alert.setTitle("Delete all?");

		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == yes) {
			System.out.println("deleteMenuID(" + menuID + ");");
			for (int i = 0; i < 7; i++) {
				controllers[i].deleteMenuID(menuID);
			}
		}

	}

	void mouseClick() {
		Boolean mouseInADayPane = false;
		for (int i = 0; i < 7 && !mouseInADayPane; i++) {
			mouseInADayPane = !controllers[i].getMouseOut();
		}
		if (menuController.getMouseInMenu() == false) {
			if (!mouseInADayPane) {
				//System.out.println("deselect time :D");
				for (int i = 0; i < 7; i++) {
					controllers[i].deselect();
				}
				menuController.deselect();
			}
		}

		//System.out.println("clicked!");
	}

	public void bigDelete(ActionEvent event) {
		for (int i = 0; i < 7; i++) {
			controllers[i].delClicked(null);
		}
	}

	@FXML
	public void handleButtonClick() {
		System.out.println("buttonClick");
	}
}
