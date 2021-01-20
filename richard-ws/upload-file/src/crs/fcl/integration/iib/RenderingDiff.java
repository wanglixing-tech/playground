package crs.fcl.integration.iib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.ListIterator;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

public class RenderingDiff extends Application {
	static String file1Text = new String();
	static String file2Text = new String();
	static String pattern = "\\&para\\;\\<br\\>";

	public static void main(String[] args) throws IOException {
		file1Text = new String(Files.readAllBytes(
				new File("C:\\Users\\Richard.Wang\\WS-HOME\\ws-java-jaxb\\CompareXMLFiles\\zoo1.xml").toPath()));
		file2Text = new String(Files.readAllBytes(
				new File("C:\\Users\\Richard.Wang\\WS-HOME\\ws-java-jaxb\\CompareXMLFiles\\zoo2.xml").toPath()));

		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Primary");
		primaryStage.setWidth(500);
		primaryStage.setHeight(500);
		VBox root = new VBox();

        Button btn = new Button("Show diff Result");
        btn.setOnAction(eve-> new DiffStage());
            
        root.getChildren().add(btn);
		Scene scene = new Scene(root,700,500);

        primaryStage.setScene(scene);
        primaryStage.show();
	}

	private static String compareFile(String text1, String text2) {

		diff_match_patch dmp = new diff_match_patch();
		dmp.Diff_Timeout = 0;
		LinkedList<Diff> difference12 = dmp.diff_main(text1, text2, true);
		//ListIterator itr12 = difference12.listIterator();
        //while(itr12.hasNext()){
        //    System.out.println(itr12.next());
        //}

		String diff12 = dmp.diff_prettyHtml(difference12);		
		return diff12;
	}

	class DiffStage {
	    
	    DiffStage()
	    {
	        Stage subStage = new Stage();
	        subStage.setTitle("Difference");
	                
	        VBox root = new VBox();
	        root.setAlignment(Pos.CENTER);
	        Scene scene = new Scene(root, 500, 500);

			final WebView browser = new WebView();
			final WebEngine webEngine = browser.getEngine();

			ScrollPane scrollPane = new ScrollPane();
			scrollPane.setContent(browser);
			String diffHtml = compareFile(file1Text, file2Text);

			webEngine.loadContent(diffHtml.replaceAll(pattern, "<br/>"));

			root.getChildren().addAll(scrollPane);
			scene.setRoot(root);
	        subStage.setScene(scene);
	        subStage.show();
	    }
	}

	private static String readFile(String filename) {
		// Read a file from disk and return the text contents.
		StringBuffer strbuf = new StringBuffer();
		try {
			FileReader input = new FileReader(filename);
			BufferedReader bufRead = new BufferedReader(input);
			String line = bufRead.readLine();
			while (line != null) {
				strbuf.append(line);
				strbuf.append('\n');
				line = bufRead.readLine();
			}

			bufRead.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return strbuf.toString();
	}
}
