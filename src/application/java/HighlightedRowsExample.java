package application.java;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class HighlightedRowsExample extends Application {

    private final ObservableList<Integer> highlightRows = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        TableView<Task> table = new TableView<>();
        table.getColumns().addAll(new TableColumn<>("ID"), new TableColumn<>("Name"), new TableColumn<>("MenuID"), new TableColumn<>("Progress"));

        // Add sample data to the table
        ObservableList<Task> data = FXCollections.observableArrayList(
                new Task(1, "John", 3, 25, null),
                new Task(2, "Jane", 34, 0, null),
                new Task(3, "Mike", 2, 35, null),
                new Task(4, "Alice", 2, 100, null)
        );
        table.setItems(data);

        table.setRowFactory(new Callback<TableView<Task>, TableRow<Task>>() {
            @Override
            public TableRow<Task> call(TableView<Task> tableView) {
                final TableRow<Task> row = new TableRow<Task>() {
                    @Override
                    protected void updateItem(Task person, boolean empty) {
                        super.updateItem(person, empty);
                        if (highlightRows.contains(getIndex())) {
                            if (!getStyleClass().contains("highlightedRow")) {
                                getStyleClass().add("highlightedRow");
                            }
                            setStyle("-fx-background-color: brown; -fx-background-insets: 0, 1, 2; -fx-background: -fx-accent; -fx-text-fill: -fx-selection-bar-text;");
                        } else {
                            getStyleClass().removeAll("highlightedRow");
                            setStyle("");
                        }
                    }
                };

                highlightRows.addListener(new ListChangeListener<Integer>() {
                    @Override
                    public void onChanged(Change<? extends Integer> change) {
                        if (highlightRows.contains(row.getIndex())) {
                            if (!row.getStyleClass().contains("highlightedRow")) {
                                row.getStyleClass().add("highlightedRow");
                            }
                            row.setStyle("-fx-background-color: brown; -fx-background-insets: 0, 1, 2; -fx-background: -fx-accent; -fx-text-fill: -fx-selection-bar-text;");
                        } else {
                            row.getStyleClass().removeAll("highlightedRow");
                            row.setStyle("");
                        }
                    }
                });

                return row;
            }
        });

        Button btnHighlight = new Button("Highlight");
        btnHighlight.setOnAction(event -> {
            highlightRows.setAll(table.getSelectionModel().getSelectedIndices());
        });

        Button btnClearHighlight = new Button("Clear Highlights");
        BooleanBinding isHighlightEmpty = Bindings.createBooleanBinding(() -> highlightRows.isEmpty(), highlightRows);
        btnClearHighlight.disableProperty().bind(isHighlightEmpty);
        btnClearHighlight.setOnAction(event -> {
            highlightRows.clear();
        });

        VBox vbox = new VBox(10, table, btnHighlight, btnClearHighlight);
        Scene scene = new Scene(vbox, 300, 200);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

