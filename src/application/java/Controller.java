package application.java;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
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
	Task<Void> task; Thread th;
	@FXML ProgressBar pg;
	@FXML Button reset;
	@FXML
	private void initialize() {
		//System.out.println("initializing....");
		//initialize connection to sql database:
		pg.setVisible(false);
		pg.setManaged(false);
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
			controllers[i].loadData();
			//controllers[i].setTableName(weekdays[i]);	//names the fxmls so they know which table in charge of
			//controllers[i].setConnect(c);	//gives each day access to a shared sql connection
			HBox.setHgrow(roots[i], Priority.ALWAYS); //allow tableview to grow if greater than pref dimensions
			if (dayOfWeekValue == i) {
				controllers[i].highlight();
			}
		}

		menuController.setMain(this);
		ruleController.setMain(this);
		menuController.setConnect(c);
		calendarController.setMain(this, c);
		//ruleController.setMain(this);
		ruleController.setConnect(c);

		for (int i = 0; i < 7; i++)  {
			if (controllers[i].hasRules() == false) {
				bigReset(null);
				break;
			}
		}
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
		/*		c.runSQL(String.format("select * FROM MasterTasks WHERE CONCAT(YEAR(day),'-',MONTH(day)) = "
						+ "'%s-%d' AND DAY(day) >= %d AND DAY(day) < %d order by day;"
						, a.getYear(),a.getMonth().getValue(), a.getDayOfMonth(), a.getDayOfMonth()+7));
				try {
					int prev = -1;
					ObservableList<MyTask> list = FXCollections.observableArrayList();
					while (c.rs.next()) {
						int cur = c.rs.getDate("day").getDate();
						if (cur != prev) {
							
						}
					}
				} catch (SQLException e) {e.printStackTrace();}*/
		for (int i = 0; i < 7; i++) {
			calControllers[i].initializeVals(weekdays[i], a.plusDays(i),c);
			calControllers[i].loadData();
			HBox.setHgrow(calRoots[i], Priority.ALWAYS); //allow tableview to grow if greater than pref dimensions
		}
		calDay = a;
	}

	public void hideCalendarWeek() {
		dayRows.getChildren().remove(calendars);
		calDay = null;
	}

	public void setMain(Main m) {
		for (int i = 0; i < 7; i++) {
			controllers[i].setMain(m, this);
		}
		for (int i = 0; i < 7; i++) {
			calControllers[i].setMain(m, this);
		}
	}

	@FXML
	public void bigReset(ActionEvent e) {
		reset.setDisable(true);

		task = new Task<Void>() {
			Connect temp = new Connect();
			@Override
			protected Void call() throws Exception {
				pg.setVisible(true);
				pg.setManaged(true);
				long ct = 0;
				long total = ruleController.getRulesCount() + ruleController.getRulesCount()*7;
				
				for (int i = 0; i < 7; i++) total += controllers[i].getNumEntries(); //each resetRules();
				
				for (int i = 0; i < 7; i++) {
					ct += controllers[i].getNumEntries();
					controllers[i].resetRules(temp);
					updateProgress(ct,total);
				}

				temp.runSQL("select * from Rules;");
				List<Rule> a = new LinkedList<Rule>();
				try {
					while(temp.rs.next()) {
						a.add(new Rule(temp.rs.getInt("id"),temp.rs.getString("name"),temp.rs.getInt("menuID"),temp.rs.getBoolean("mon"), temp.rs.getBoolean("tues"), 
								temp.rs.getBoolean("wed"), temp.rs.getBoolean("thurs"), temp.rs.getBoolean("fri"), temp.rs.getBoolean("sat"), temp.rs.getBoolean("sun"), null));
						updateProgress(ct++,total);
					}
				} catch (SQLException e1) {e1.printStackTrace();}
				for (Rule r : a) {
					if (r.isMon()) controllers[0].sqlAddEntry(r.getName(), r.getMenuID(), r.getId(), temp);
					updateProgress(ct++,total);
					if (r.isTues()) controllers[1].sqlAddEntry(r.getName(), r.getMenuID(), r.getId(), temp);
					updateProgress(ct++,total);
					if (r.isWed()) controllers[2].sqlAddEntry(r.getName(), r.getMenuID(), r.getId(), temp);
					updateProgress(ct++,total);
					if (r.isThurs()) controllers[3].sqlAddEntry(r.getName(), r.getMenuID(), r.getId(), temp);
					updateProgress(ct++,total);
					if (r.isFri()) controllers[4].sqlAddEntry(r.getName(),  r.getMenuID(),r.getId(), temp);
					updateProgress(ct++,total);
					if (r.isSat()) controllers[5].sqlAddEntry(r.getName(),  r.getMenuID(),r.getId(), temp);
					updateProgress(ct++,total);
					if (r.isSun()) controllers[6].sqlAddEntry(r.getName(), r.getMenuID(), r.getId(), temp);
					updateProgress(ct++,total);
				}
				for (int i = 0; i < 7; i++) {
					controllers[i].loadData(temp);
				}
				System.out.println("done" + " progress = " + ct + "/" + total);
				reset.setDisable(false);
				pg.setVisible(false);
				pg.setManaged(false);
				return null;
			}
		};
		th = new Thread(task);
		th.setDaemon(true);
		th.start();
		pg.progressProperty().bind(task.progressProperty());
	}

	public void highlightMenuID(int menuID) {
		System.out.println("highlightMenuID(" + menuID + ");");
		for (int i = 0; i < 7; i++) {
			controllers[i].highlightMenu(menuID);
		}
		ruleController.highlightMenu(menuID);
	}

	public void highlightRuleID(long l) {
		System.out.println("highlightMenuID(" + l + ");");
		for (int i = 0; i < 7; i++) {
			controllers[i].highlightRule(l);
		}
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
		if (menuController.getMouseInMenu() == false && ruleController.getMouseInRule() == false) {
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

	public void highlightCal(LocalDate day) {
		System.out.println("highlightCal(" + day + ");");
		calendarController.highlightCal(day);

	}

	public void unhighlightCal(LocalDate day) {
		System.out.println("unhighlightCal(" + day + ");");
		calendarController.unhighlightCal(day);
	}
}
