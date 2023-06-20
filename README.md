# DreamTeam

### 6-20-2023

- setting relative dimensions between components:
	- in fxml:

		```
		<BorderPane fx:id="stage">;
			<Button prefWidth="${stage.width*0.7}"&gt; </Button>
		</BorderPane>
		```

	- in java:
		
		```
		button.prefWidthProperty().bind(stage.widthProperty());
		```

- debugging addListeners to any property: 

	```
	addButton.widthProperty().addListener((ChangeListener<? super Number>) new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> obs, Number oldVal, Number newVal) {
			System.out.println(newVal);
		}
    });
	```

- specify border thickness for each side:
	```
	label.setStyle("-fx-border-width: 1 2 3 4; -fx-border-color: black;");
	```

- fixing component not growing beyond preferred dimensions:
	```
	HBox.setHgrow(root, Priority.ALWAYS); 
	```


### 6-19-2023

- quick workaround to failing auto-generate controller code:
	1. open target fxml in SceneBuilder
	2. go to View -> Show Sample Controller Skeleton
	3. the above skeleton setup will all be taken care of for you. 

- linking up ids between controller and fxml:
	- controller:
		```
		<BorderPane fx:controller="application.java.Controller"> 
			<!--content--> 
			<Button fx:id="butt" onAction="#buttonClicked" text="Button"/>
		</BorderPane>
		```
	- fxml: 
		```
		public class Controller {
			@FXML
			Button butt;

			void buttonClicked(ActionEvent event) {
				System.out.println("Button clicked!");
				butt.setText(String.valueOf(getRandom()));
			}
    	}
		```

- create instance of & manipulate sub-fxml:
	```
	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/resources/pane-1.fxml"));   //how-to abs path       
	Pane1Controller controller = fxmlLoader.<Pane1Controller>getController();				//handle to manipulate THIS instance
	controller.setSomething("param");									//manipulate w/ setter/getter
	myPane.getChildren().add(fxmlLoader.load());								//loads sub-fxml
	```
- how to properly organize and link up files in project:
	- module-info.java ALWAYS in same folder as where class containing main() is. 
	- update module-info.java for any folders with java files:
		
		if java file in src/application/java:
		
		```
		opens applications.java to javafx.fxml, javafx.graphics, ..etc.. ;
		```
	- finding controller in fxml:

		if borderPane is overarching component of fxml and controller in src/application/java:

		```
		<BorderPane fx:controller="application.java.Controller">
		```

	- finding fxml in java files (abs path starting at src):

		```
		try {
			root = FXMLLoader.load(getClass().getResource("/application/resources/main-view.fxml"));
		} catch (IOException e) {e.printStackTrace();}
		```

### 6-18-2023

Hello :) my first time using SceneBuilder and FXML