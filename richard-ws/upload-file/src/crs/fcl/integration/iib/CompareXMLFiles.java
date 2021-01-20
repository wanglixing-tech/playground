package crs.fcl.integration.iib;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;

public class CompareXMLFiles extends Application {
	static String fileName1 = new String();
	static String fileName2 = new String();
	static String pattern = "\\&para\\;\\<br\\>";

	@Override
	public void start(final Stage primaryStage) throws Exception {
		final FileChooser fileChooser = new FileChooser();
        final GridPane grid = new GridPane();

		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(5);
		grid.setHgap(5);
		// Defining the Name text field
		final TextField source1 = new TextField();
		source1.setPromptText("Enter source1 to compare.");
		source1.setPrefColumnCount(10);
		source1.getText();
		GridPane.setConstraints(source1, 0, 0);
		grid.getChildren().add(source1);
		// Defining the Last Name text field
		final TextField source2 = new TextField();
		source2.setPromptText("Enter source2 to compare.");
		GridPane.setConstraints(source2, 0, 1);
		grid.getChildren().add(source2);

		// Defining the Submit button
		Button btnSource1 = new Button("Select source 1");
		btnSource1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				configureFileChooser(fileChooser);
				File file = fileChooser.showOpenDialog(primaryStage);
				if (file != null) {
					source1.setText(file.getPath());
					fileName1 = file.getPath();
					// openFile(file);
				}
			}
		});

		GridPane.setConstraints(btnSource1, 1, 0);
		grid.getChildren().add(btnSource1);
		// Defining the Clear button
		Button btnSource2 = new Button("Select source 2");
		btnSource2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				configureFileChooser(fileChooser);
				File file = fileChooser.showOpenDialog(primaryStage);
				if (file != null) {
					source2.setText(file.getPath());
					fileName2 = file.getPath();
					// openFile(file);
				}
			}
		});

		GridPane.setConstraints(btnSource2, 1, 1);
		grid.getChildren().add(btnSource2);

		// Defining the Submit button
		Button compareDiff = new Button("Show difference");
		GridPane.setConstraints(compareDiff, 0, 2);
		grid.getChildren().add(compareDiff);
		// Defining the Clear button
		Button clear = new Button("Clear");
		GridPane.setConstraints(clear, 1, 2);
		grid.getChildren().add(clear);

		// Adding a Label
		final Label label = new Label();
		GridPane.setConstraints(label, 0, 3);
		GridPane.setColumnSpan(label, 2);
		grid.getChildren().add(label);

		// Setting an action for the Submit button
		compareDiff.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				if ((source1.getText() != null && !source1.getText().isEmpty())) {
					if ((source2.getText() != null && !source2.getText().isEmpty())) {

						label.setText("Compare Following two files:\n " + source1.getText() + "\n" + source2.getText());
						try {
							showDiffScreen();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						label.setText("Source2 is empty.");
					}
				} else {
					label.setText("Source1 is empty.");
				}
			}
		});

		// Setting an action for the Clear button
		clear.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				source1.clear();
				source2.clear();
				label.setText(null);
			}
		});

        Group rootGroup = new Group();
        rootGroup.getChildren().addAll(grid);
        Scene scene = new Scene(rootGroup ,600, 300); 
		//stage.setScene(new Scene(grid, 800, 500));
        Image image = new Image(new FileInputStream("C:\\Users\\Richard.Wang\\Pictures\\fcl-logo.gif")); 
        primaryStage.getIcons().add(image);

        primaryStage.setScene(scene);
        scene.setFill(Color.BEIGE);
        primaryStage.show();
	}

	public static void main(String[] args) throws Exception {

		launch(args);
	}

	public void showDiffScreen() throws IOException {
		Stage subStage = new Stage();
		subStage.setTitle("Difference");

		VBox root = new VBox();
		root.setAlignment(Pos.CENTER);
		Scene scene = new Scene(root, 800, 500);

		final WebView browser = new WebView();
		final WebEngine webEngine = browser.getEngine();

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(browser);
		String file1Text = new String(Files.readAllBytes(new File(fileName1).toPath()));
		String file2Text = new String(Files.readAllBytes(new File(fileName2).toPath()));

		String diffHtml = compareFile(file1Text, file2Text);

		webEngine.loadContent(diffHtml.replaceAll(pattern, "<br/>"));

		root.getChildren().addAll(scrollPane);
		scene.setRoot(root);
		subStage.setScene(scene);
		subStage.show();
	}
	
	private static void configureFileChooser(final FileChooser fileChooser) {
		fileChooser.setTitle("Select Dynamic Configuration sources");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML", "*.xml"));
	}

	private static String compareFile(String text1, String text2) {

		diff_match_patch dmp = new diff_match_patch();
		dmp.Diff_Timeout = 0;
		LinkedList<Diff> difference12 = dmp.diff_main(text1, text2, true);
		// ListIterator itr12 = difference12.listIterator();
		// while(itr12.hasNext()){
		// System.out.println(itr12.next());
		// }

		String diff12 = dmp.diff_prettyHtml(difference12);
		return diff12;
	}

}