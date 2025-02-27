package org.example.keystone.api;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainApplicationController implements Initializable {

    /*
        This is the Main Application Controller

        Here we can control the logic of our UI. @FXML annotated methods and variables are linked to the corresponding elements defined
        in the UI's FXML file, allowing us to interact with our UI elements using java code.

        If using scenebuilder, you must set the fx:id to a unique value, then create a corresponding variable here with matching type
        and name. This can also be specified directly in the fxml file.
     */
    @FXML
    private Label welcomeText;

    @FXML
    private ScrollPane fileScrollPane;

    @FXML
    private VBox fileVBox;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String directoryPath = "src/images";
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
               for (File file : files) {
                   Text fileNameText = new Text(file.getName());
                   fileNameText.setOnMouseClicked(event -> {
                       // here we need to add some logic to fetch the metadata for the file and send it to be displayed
                       // by another UI component
                   });
                   fileVBox.getChildren().add(fileNameText);
               }
            }
        } else {
            fileVBox.getChildren().add(new Text("Directory not found"));
        }
    }
}