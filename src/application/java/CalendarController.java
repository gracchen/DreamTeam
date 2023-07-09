package application.java;

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
	
	public void renderCalendar() {
	    Node node = grid.getChildren().get(0);
	    grid.getChildren().clear();
	    grid.getChildren().add(0,node);
		LocalDate cur = LocalDate.of(curYear, curMonth, 1);
		//System.out.println(curMonth + " 1, " + curYear + " is on a " + cur.getDayOfWeek() + " which is " + (cur.getDayOfWeek().getValue()-1));
		//System.out.println("\tand has " + cur.lengthOfMonth() + " days");
		
		int j = 0;
		Label l1 = new Label("1");
		if (LocalDate.now().equals(cur))
			l1.setStyle("-fx-background-color: yellow;");
		

		l1.setMaxWidth(100000);
		l1.setMaxHeight(100000);
		l1.setAlignment(Pos.CENTER);
		grid.add(l1, (cur.getDayOfWeek().getValue()-1), j);
		GridPane.setHalignment(l1, HPos.CENTER);
		labels.add(l1);
		l1.setOnMouseClicked(e -> {
			System.out.println(curMonth + " " + l1.getText() + ", " + curYear);
			LocalDate selected = LocalDate.of(curYear,curMonth,Integer.valueOf(l1.getText()));
			while (selected.getDayOfWeek() != DayOfWeek.MONDAY) {
				selected = selected.minusDays(1);
			}
			c.showCalendarWeek(selected);
		});
		
		if (LocalDate.now().equals(cur))
		{
			l1.setStyle("-fx-background-color: yellow;");
			l1.getStyleClass().add("yellow");
		}
		if (LocalDate.now().isBefore(cur)) {
			cc.runSQL(String.format("select * from MasterTasks where day='%s';", cur));		
			try {
				if(cc.rs.next()) {
					l1.setStyle("-fx-background-color: lightblue;");
					l1.getStyleClass().add("lightblue");
				}
			} catch (SQLException e1) {e1.printStackTrace();}
		}
		
		l1.setOnMouseEntered(e -> {l1.setStyle("-fx-background-color: lightgray;");});

		l1.setOnMouseExited(e -> {l1.setStyle((l1.getStyleClass().contains("lightblue"))? 
				"-fx-background-color: lightblue;": (l1.getStyleClass().contains("yellow"))? "-fx-background-color: yellow" : null);});
		GridPane.setHalignment(l1, HPos.CENTER);
		
		for (int i = 1; i < cur.lengthOfMonth(); i++) {
			if (cur.plusDays(i).getDayOfWeek() == DayOfWeek.MONDAY) {
				j++;
			}
			Label l = new Label(String.valueOf(i+1));
			if (LocalDate.now().equals(cur.plusDays(i)))
			{
				l.setStyle("-fx-background-color: yellow;");
				l.getStyleClass().add("yellow");
			}
			if (LocalDate.now().isBefore(cur.plusDays(i))) {
				cc.runSQL(String.format("select * from MasterTasks where day='%s';", cur.plusDays(i)));		
				try {
					if(cc.rs.next()) {
						l.setStyle("-fx-background-color: lightblue;");
						l.getStyleClass().add("lightblue");
					}
				} catch (SQLException e1) {e1.printStackTrace();}
			}
				
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

}
