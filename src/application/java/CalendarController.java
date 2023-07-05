package application.java;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
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
	Month curMonth;
	int curYear;
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
		renderCalendar();
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
	
	void renderCalendar() {
	    Node node = grid.getChildren().get(0);
	    grid.getChildren().clear();
	    grid.getChildren().add(0,node);
		LocalDate cur = LocalDate.of(curYear, curMonth, 1);
		//System.out.println(curMonth + " 1, " + curYear + " is on a " + cur.getDayOfWeek() + " which is " + (cur.getDayOfWeek().getValue()-1));
		//System.out.println("\tand has " + cur.lengthOfMonth() + " days");
		
		int j = 0;
		Label l1 = new Label("1");
		grid.add(l1, (cur.getDayOfWeek().getValue()-1), j);
		GridPane.setHalignment(l1, HPos.CENTER);
		
		for (int i = 1; i < cur.lengthOfMonth(); i++) {
			if (cur.plusDays(i).getDayOfWeek() == DayOfWeek.MONDAY) {
				j++;
			}
			Label l = new Label(String.valueOf(i+1));
			GridPane.setHalignment(l, HPos.CENTER);
			grid.add(l,(cur.plusDays(i).getDayOfWeek().getValue()-1),j);
		}
	}

	void setLabel() {
		String text = curMonth.toString();
		text = String.valueOf(text.charAt(0)) + text.substring(1).toLowerCase();
		monthLabel.setText(text);
	}

}
