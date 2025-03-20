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

    public String directoryPath = "src/images/";
    

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private TreeItem<XMLTreeNode> convertXMLNodeToTreeItem(XMLNode node, String parentPath) {

        /*
            parse through an XMLNode Tree and create TreeItems. We only want data from nodes that are CXT_Element types
            or CXT_Text types. Elements will be our nodes and the text contained in them will be the value for that node.

            We also need to keep track of the path to each node, so that we can directly search for it when updating
            values that have been modified by the user.
         */

        if (node == null) {
            return null;
        }

        if (!node.getType().toString().equals("CXT_Element")) {
            return null;
        }

        String nodeName = node.getValue();
        if (nodeName.startsWith("gml:")) {
            nodeName = nodeName.substring(4);
        }

        if (nodeName.equals("name")) {
            nodeName = "ESPG";
        }


        String nodePath = parentPath.isEmpty() ? nodeName : parentPath + "/" + nodeName;

        XMLTreeNode treeNode = new XMLTreeNode(nodeName, "", nodePath);
        TreeItem<XMLTreeNode> treeItem = new TreeItem<>(treeNode);

        StringBuilder textBuilder = new StringBuilder();

        XMLNode child = node.getChild();
        while (child != null) {
            String childType = child.getType().toString();

            if ("CXT_Text".equals(childType)) {
                textBuilder.append(child.getValue());
            } else if ("CXT_Element".equals(childType)) {
                TreeItem<XMLTreeNode> childItem = convertXMLNodeToTreeItem(child, nodePath);
                if (childItem != null) {
                    treeItem.getChildren().add(childItem);
                }
            }

            child = child.getNext();
        }

        String nodeValue = textBuilder.toString().trim();
        treeNode.nodeValueProperty().set(nodeValue);
        treeItem.setExpanded(true);

        return treeItem;
    }



    private Vector<TreeItem<XMLTreeNode>> convertHashTableToTreeEntries(Hashtable<String, String> table) {
        /*
            Converts a hashtable into a vector of TreeItems containing the key-value pairs from the hashtable
         */
        Vector<TreeItem<XMLTreeNode>> vector = new Vector<>();
        for (String key : table.keySet()) {
            TreeItem<XMLTreeNode> item = new TreeItem<>(new XMLTreeNode(key, table.get(key), ""));
            vector.add(item);
        }
        return vector;
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

                if (!cellFile.isDirectory()) {
                    String filePath = cellFile.getAbsolutePath();
                    try {
                        Image image = ImageFactory.getFXImage(cellFile);
                        imageViewer.setImage(image);
                    } catch (Exception e) {
                        System.err.println("Error Loading Image: " + e);
                    }
                    loadMetadata(filePath);
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

    public void loadMetadata(String filePath) {
        try {
            File file = new File(filePath);
            MetadataDecoder metadataDecoder = MetadataDecoderFactory.createDecoder(file);

            if (metadataDecoder == null) {
                throw new IllegalArgumentException("Unsupported file format, or unable to create decoder");
            }

            TreeItem<XMLTreeNode> topNode = new TreeItem<>(new XMLTreeNode("Metadata", "", ""));
            TreeItem<XMLTreeNode> rootNode = new TreeItem<>(new XMLTreeNode("Spatial Reference Metadata", "", ""));
            TreeItem<XMLTreeNode> spatialRefRootNode = null;
            // Spatial Ref
            try {
                String spatialReferenceXML = metadataDecoder.getSpatialReferenceXML();

                if (spatialReferenceXML != null && !spatialReferenceXML.isEmpty())  {
                    XMLNode xmlRootNode = gdal.ParseXMLString(spatialReferenceXML);
                    spatialRefRootNode = convertXMLNodeToTreeItem(xmlRootNode, "");
                }

            } catch (Exception e) {
                System.err.println("Failed to retrieve spatial reference metadata for " + file.getName());
            }
            // Metadata Fields

            TreeItem<XMLTreeNode> metadataRootNode = new TreeItem<>(new XMLTreeNode("Metadata Domains", "", ""));
            try {
                Vector<String> metadataDomains = metadataDecoder.getMetadataDomains();

                for (String domain : metadataDomains) {
                    TreeItem<XMLTreeNode> domainNode;
                    if (domain.isEmpty()) {
                        domainNode = new TreeItem<>(new XMLTreeNode("DEFAULT", "", ""));
                    } else {
                        domainNode = new TreeItem<>(new XMLTreeNode(domain, "", ""));
                    }
                    Hashtable<String, String> table = metadataDecoder.getMetadataHashTable(domain);
                    Vector<TreeItem<XMLTreeNode>> treeItems = convertHashTableToTreeEntries(table);
                    for (TreeItem<XMLTreeNode> item : treeItems) {
                        domainNode.getChildren().add(item);
                    }
                    domainNode.setExpanded(true);
                    metadataRootNode.getChildren().add(domainNode);
                }
            } catch (Exception e) {
                System.err.println("Failed to retrieve metadata domains");
            }

            TreeItem<XMLTreeNode> geoTransformRootNode = new TreeItem<>(new XMLTreeNode("Affine Transformation Data", "", ""));
            try {
                Hashtable<String, String> affineTransforms = metadataDecoder.getGeoTransform();
                Vector<TreeItem<XMLTreeNode>> items = convertHashTableToTreeEntries(affineTransforms);
                for (TreeItem<XMLTreeNode> item : items) {
                    geoTransformRootNode.getChildren().add(item);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            TreeItem<XMLTreeNode> cornerCoordinatesRootNode = new TreeItem<>(new XMLTreeNode("Corner Coordinates", "", ""));
            try {
                Hashtable<String, String> cornerCoordinates = metadataDecoder.getCornerCoordinates();
                Vector<TreeItem<XMLTreeNode>> items = convertHashTableToTreeEntries(cornerCoordinates);
                for (TreeItem<XMLTreeNode> item : items) {
                    cornerCoordinatesRootNode.getChildren().add(item);
                }
            } catch (Exception e) {
                System.err.println("Error when loading corner coordinates");
            }


            if (spatialRefRootNode != null) {
                TreeItem<XMLTreeNode> spatialrefNode = new TreeItem<>(new XMLTreeNode("Spatial Reference","",""));
                spatialrefNode.getChildren().add(spatialRefRootNode);
                rootNode.getChildren().add(spatialrefNode);
            }

            if (geoTransformRootNode != null) {
                rootNode.getChildren().add(geoTransformRootNode);
            }

            if (cornerCoordinatesRootNode != null) {
                rootNode.getChildren().add(cornerCoordinatesRootNode);
            }

            if (metadataRootNode != null) {
                topNode.getChildren().add(metadataRootNode);
            }
            topNode.getChildren().add(rootNode);
            metadataTable.setRoot(topNode);
            metadataTable.setShowRoot(false);

            metadataTableKeyCol.setCellValueFactory(param -> param.getValue().getValue().nodeNameProperty());
            metadataTableValueCol.setCellValueFactory(param -> param.getValue().getValue().nodeValueProperty());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}