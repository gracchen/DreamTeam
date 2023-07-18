package application.java;

import java.sql.Date;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class CalendarController {

	@FXML private Spinner<Integer> yearSelector;
	@FXML private BorderPane stage;    
	@FXML private Button leftButton, rightButton;    
	@FXML private Label monthLabel;
	@FXML private GridPane grid;
	@FXML private List<Label> labels = new ArrayList<Label>();
	Month curMonth;
	int curYear;
	Controller c;
	Connect cc;
	private PauseTransition delay;
	@FXML
	void initialize() {
		curMonth = LocalDate.now().getMonth();
		curYear = LocalDate.now().getYear();
		setLabel();

		yearSelector.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1970, curYear+5, curYear));


		delay = new PauseTransition(Duration.millis(300)); // Adjust the duration as needed

		yearSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
			delay.stop();
			delay.playFromStart();
			delay.setOnFinished(event -> {
				System.out.println("New value: " + newValue);
				curYear = newValue;
				renderCalendar();
			});
		});
		//renderCalendar();
	}

	@FXML
	void leftClicked(ActionEvent e) {
		if (curMonth == Month.JANUARY) { 
			if (curYear != 1970) {
				curYear--;
				yearSelector.decrement();
			} else { return; }
		}

		curMonth = curMonth.minus(1);
		setLabel();
		renderCalendar();
	}

	@FXML
	void rightClicked(ActionEvent e) {
		//System.out.println(curMonth + " " + curYear);
		if (curMonth == Month.DECEMBER) {
			if (curYear != LocalDate.now().getYear()+5) {
				curYear++;
				yearSelector.increment();
			} else { return; }
		}

		curMonth = curMonth.plus(1);
		setLabel();
		renderCalendar();
	}
	
	@SuppressWarnings("deprecation")
	public void renderCalendar() {
		Label today = null;
		labels.clear();
	    Node node = grid.getChildren().get(0);
	    grid.getChildren().clear();
	    grid.getChildren().add(0,node);
		LocalDate cur = LocalDate.of(curYear, curMonth, 1);
		
		int j = 0;
		
		for (int i = 0; i < cur.lengthOfMonth(); i++) {
			if (cur.plusDays(i).getDayOfWeek() == DayOfWeek.MONDAY && i != 0) j++;

			Label l = new Label(String.valueOf(i+1));
			
			if (LocalDate.now().equals(cur.plusDays(i))) today = l;
				
			l.setMaxWidth(100000); l.setMaxHeight(100000); l.setAlignment(Pos.CENTER);
			labels.add(l);
			l.setOnMouseClicked(e -> {
				System.out.println(curMonth + " " + l.getText() + ", " + curYear);
				LocalDate selected = LocalDate.of(curYear,curMonth,Integer.valueOf(l.getText()));
				while (selected.getDayOfWeek() != DayOfWeek.MONDAY) {
					selected = selected.minusDays(1);
				}
				c.showCalendarWeek(selected);
			});
			l.setOnMouseEntered(e -> {l.setStyle("-fx-background-color: lightgray;");});
			l.setOnMouseExited(e -> {l.setStyle((l.getStyleClass().contains("lightblue"))? 
					"-fx-background-color: lightblue;": (l.getStyleClass().contains("yellow"))? "-fx-background-color: yellow" : null);});
			GridPane.setHalignment(l, HPos.CENTER);
			grid.add(l,(cur.plusDays(i).getDayOfWeek().getValue()-1),j);
		}
		
		cc.runSQL(String.format("select distinct day FROM MasterTasks WHERE CONCAT(YEAR(day),'-',MONTH(day)) = '%s-%d' order by day;", curYear, curMonth.getValue()));
		try {
			while (cc.rs.next()) {
				int cur2 = cc.rs.getDate("day").getDate();
				labels.get(cur2-1).setStyle("-fx-background-color: lightblue;");
				labels.get(cur2-1).getStyleClass().add("lightblue");
			}
		} catch (SQLException e) {e.printStackTrace();}
		
		if (today != null) {
			today.getStyleClass().removeAll("lightblue");
			today.setStyle("-fx-background-color: yellow;");
			today.getStyleClass().add("yellow");
		}
	}
	
	void setMain(Controller c, Connect cc) {
		this.c = c;
		this.cc = cc;
		renderCalendar();
	}
	
	void setLabel() {
		String text = curMonth.toString();
		text = String.valueOf(text.charAt(0)) + text.substring(1).toLowerCase();
		monthLabel.setText(text);
	}

	
	public void highlightCal(LocalDate day) {
		if (day.getMonth() == curMonth && !labels.get(day.getDayOfMonth()-1).getStyleClass().contains("lightblue")) {
			labels.get(day.getDayOfMonth()-1).setStyle("-fx-background-color: lightblue;");
			labels.get(day.getDayOfMonth()-1).getStyleClass().add("lightblue");
		}
	}
	
	public void unhighlightCal(LocalDate day) {
		if (day.getMonth() == curMonth && labels.get(day.getDayOfMonth()-1).getStyleClass().contains("lightblue")) {
			labels.get(day.getDayOfMonth()-1).setStyle(null);
			labels.get(day.getDayOfMonth()-1).getStyleClass().removeAll("lightblue");
		}
	}

}
