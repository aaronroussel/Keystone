package org.example.keystone.api;

import com.sun.source.tree.Tree;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.gdal.gdal.XMLNode;
import org.gdal.gdal.gdal;


import java.awt.*;
import java.beans.EventHandler;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;

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
    public TreeView<File> fileDirectoryTreeView;

    @FXML
    private ScrollPane fileScrollPane;

    @FXML
    private VBox fileVBox;

    @FXML
    public ImageView imageViewer;

    @FXML
    public TreeTableView<XMLTreeNode> metadataTable;
    //public TreeTableView<MetadataEntry> metadataTable;

    @FXML
    public TreeTableColumn<XMLTreeNode, String> metadataTableKeyCol;
    //public TreeTableColumn<MetadataEntry, String> metadataTableKeyCol;
    @FXML
    public TreeTableColumn<XMLTreeNode, String> metadataTableValueCol;
    //public TreeTableColumn<MetadataEntry, String> metadataTableValueCol;

    @FXML
    public AnchorPane imagePreviewAnchorPane;



    public String directoryPath = "src/images/";
    

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    // bind width to allow resizing with splitpane
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageViewer.fitWidthProperty().bind(imagePreviewAnchorPane.widthProperty());


        metadataTable.prefWidthProperty().bind(imagePreviewAnchorPane.widthProperty());

        metadataTableKeyCol.prefWidthProperty().bind(imagePreviewAnchorPane.widthProperty().divide(2));
        metadataTableValueCol.prefWidthProperty().bind(imagePreviewAnchorPane.widthProperty().divide(2));
    }


    private void populateFileDirectoryTreeView(File directory) {
        TreeItem<File> rootItem = new TreeItem<>(directory);
        rootItem.setExpanded(true);

        fileDirectoryTreeView.setRoot(rootItem);

        fileDirectoryTreeView.setCellFactory(param -> {
            TextFieldTreeCell<File> cell = new TextFieldTreeCell<>();
            cell.setConverter(new StringConverter<File>() {
                @Override
                public String toString(File file) {
                    return file == null ? "" : file.getName();
                }

                @Override
                public File fromString(String s) {
                    return null;
                }

            });
            cell.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                File cellFile = cell.getItem();



                if (cellFile != null && !cellFile.isDirectory()) {
                    String filePath = cellFile.getAbsolutePath();
                    try {
                        Image image = ImageFactory.getFXImage(cellFile);

                        imageViewer.setImage(image);

                    } catch (Exception e) {
                        System.err.println("Error Loading Image: " + e);
                    }
                    MetadataTreeBuilder.buildTree(filePath, metadataTable, metadataTableKeyCol, metadataTableValueCol);
                }
            });

            return cell;
        });

        createTree(directory, rootItem);
    }

    private void createTree(File dir, TreeItem<File> parentItem) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                TreeItem<File> childItem = new TreeItem<>(file);

                parentItem.getChildren().add(childItem);

                if (file.isDirectory()) {
                    createTree(file, childItem);
                }
            }
        }
    }

    @FXML
    public void chooseDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Keystone Select Folder");

        File defaultDirectory = new File(directoryPath);
        chooser.setInitialDirectory(defaultDirectory);

        File selectedDirectory = chooser.showDialog(new Stage());

        directoryPath = selectedDirectory.getAbsolutePath();

        loadDirectory();
    }

    @FXML
    public void exit() {
        Platform.exit();
        System.exit(0);
    }

    public void loadDirectory() {

        File directory = new File(directoryPath);
        populateFileDirectoryTreeView(directory);
        clearMetadata();
    }

    public void clearMetadata() {

        TreeItem<XMLTreeNode> topNode = new TreeItem<>(new XMLTreeNode("Metadata", "", ""));

        metadataTable.setRoot(topNode);
        metadataTable.setShowRoot(false);

    }

}