package crs.fcl.integration.iib;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
 
public final class FileChooserSample extends Application {
 
    private Desktop desktop = Desktop.getDesktop();
 
    @Override
    public void start(final Stage primaryStage) throws FileNotFoundException {
        primaryStage.setTitle("Select Base Dynamic Configuration File");
 
        final FileChooser fileChooser = new FileChooser();
        final Button openButton = new Button("Select a file...");
      
        openButton.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                    configureFileChooser(fileChooser);
                    File file = fileChooser.showOpenDialog(primaryStage);
                    if (file != null) {
                        openFile(file);
                    }
                }
            });
 
        final GridPane inputGridPane = new GridPane();
 
        GridPane.setConstraints(openButton, 0, 1);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(openButton);
        Group rootGroup = new Group();
        //final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane);
        //rootGroup.setPadding(new Insets(12, 12, 12, 12));
        Scene scene = new Scene(rootGroup ,600, 300); 
        Image image = new Image(new FileInputStream("C:\\Users\\Richard.Wang\\Pictures\\fcl-logo.gif")); 
        primaryStage.getIcons().add(image);

        primaryStage.setScene(scene);
        scene.setFill(Color.BEIGE);
        primaryStage.show();
    }
 
    public static void main(String[] args) {
    	
        Application.launch(args);
    }
 
    private static void configureFileChooser(
        final FileChooser fileChooser) {      
            fileChooser.setTitle("View Pictures");
            fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
            );                 
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
            );
    }
 
    private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {
            Logger.getLogger(FileChooserSample.class.getName()).log(
                Level.SEVERE, null, ex
            );
        }
    }
}