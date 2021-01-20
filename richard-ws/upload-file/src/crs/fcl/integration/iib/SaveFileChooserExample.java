package crs.fcl.integration.iib;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.control.TextArea;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveFileChooserExample
		extends Application {

	private Text actionStatus;
	private Stage savedStage;
	private TextArea txtArea;
	private static final String titleTxt = "JavaFX File Chooser Example 2";
	private static final String defaultFileName = "MyFile.txt";

	public static void main(String [] args) {

		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
	
		primaryStage.setTitle(titleTxt);	

		// Window label
		Label label = new Label("Save File Chooser");
		label.setTextFill(Color.DARKBLUE);
		label.setFont(Font.font("Calibri", FontWeight.BOLD, 36));
		HBox labelHb = new HBox();
		labelHb.setAlignment(Pos.CENTER);
		labelHb.getChildren().add(label);
		
 		// Text area in a scrollpane and label
		Label txtAreaLabel = new Label("Enter text and save as a file:");
		txtAreaLabel.setFont(Font.font("Calibri", FontWeight.NORMAL, 20));
		txtArea = new TextArea();
		txtArea.setWrapText(true);
		ScrollPane scroll = new ScrollPane();
		scroll.setContent(txtArea);
		scroll.setFitToWidth(true);
		scroll.setFitToHeight(true);
		scroll.setPrefHeight(150);
		scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		
		VBox txtAreaVbox = new VBox(5);
		txtAreaVbox.setPadding(new Insets(5, 5, 5, 5));
		txtAreaVbox.getChildren().addAll(txtAreaLabel, scroll);

		// Button
		Button btn1 = new Button("Save as file...");
		btn1.setOnAction(new SaveButtonListener());
		HBox buttonHb1 = new HBox(10);
		buttonHb1.setAlignment(Pos.CENTER);
		buttonHb1.getChildren().addAll(btn1);

		// Status message text
		actionStatus = new Text();
		actionStatus.setFont(Font.font("Calibri", FontWeight.NORMAL, 20));
		actionStatus.setFill(Color.FIREBRICK);

		// Vbox
		VBox vbox = new VBox(30);
		vbox.setPadding(new Insets(25, 25, 25, 25));
		vbox.getChildren().addAll(labelHb, txtAreaVbox, buttonHb1, actionStatus);

		// Scene
		Scene scene = new Scene(vbox, 600, 400); // w x h
		primaryStage.setScene(scene);
		primaryStage.show();

		savedStage = primaryStage;
	}

	private class SaveButtonListener implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent e) {

			showSaveFileChooser();
		}
	}

	private void showSaveFileChooser() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save file");
		fileChooser.setInitialFileName(defaultFileName);
		File savedFile = fileChooser.showSaveDialog(savedStage);

		if (savedFile != null) {

			try {
				saveFileRoutine(savedFile);
			}
			catch(IOException e) {
			
				e.printStackTrace();
				actionStatus.setText("An ERROR occurred while saving the file!" +
						savedFile.toString());
				return;
			}
			
			actionStatus.setText("File saved: " + savedFile.toString());
		}
		else {
			actionStatus.setText("File save cancelled.");
		}
	}
	
	private void saveFileRoutine(File file)
			throws IOException{
		// Creates a new file and writes the txtArea contents into it
		String txt = txtArea.getText();
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		writer.write(txt);
		writer.close();
	}
}