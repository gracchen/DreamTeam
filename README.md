# DreamTeam

### 6-21-2023

- to generate automatic setters, getters, and cnostructor for a class:
	- Right click on code editor --> Source --> Generate ...

- how to configure edit for tableview w/out massive custom tableview:
	```
    	//set data to display
    	percentCol.setCellValueFactory(new PropertyValueFactory<Task,Integer>("progress"));
    	taskCol.setCellValueFactory(new PropertyValueFactory<Task,String>("name"));

    	//set editable using textfield and built in integer conversion
    	percentCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
    	taskCol.setCellFactory(TextFieldTableCell.<Task>forTableColumn());
    	
		//custom edit code
    	taskCol.setOnEditCommit(e -> {
    		c.runSQL("update " + tableName.get() + " set name = \"" + e.getNewValue() + "\" where id = " + e.getRowValue().getId());
    		loadData();
    	});
    	
    	tableview.setItems(list);
	```

- detecting mouse clicked inside a component in fxml vs root of a scene:
	- in fxml: if clicking a component on top of that component, then not count as "clicked"
	- in root java: clicking anywhere withing the bounds of the scene will trigger your function

- easy way of removing from a tableview:
	1. get all the row items (not indexes): 
	
		```
		ObservableList<Task> selectedItems = tableview.getSelectionModel().getSelectedItems();
		```
	2. loop through all selected items, get id, and delete from sql database:
	
		```
		for (Task task : selectedItems) {
    	    	c.runSQL(String.format("delete from %s where id=%d", tableName.get(), task.getId()));
    	}
		```
	3. use removeAll builtin to remove from tableview's list: 
	
		```
		list.removeAll(selectedItems);
		```


### 6-20-2023

- setting relative dimensions between components:
	- in fxml:

		```
		<BorderPane fx:id="stage">;
			<Button prefWidth="${stage.width*0.7}"> </Button>
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