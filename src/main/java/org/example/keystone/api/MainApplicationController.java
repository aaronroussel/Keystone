package org.example.keystone.api;

import com.sun.source.tree.Tree;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import org.gdal.gdal.XMLNode;
import org.gdal.gdal.gdal;


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
    public TreeTableView<XMLTreeNode> metadataTable;
    //public TreeTableView<MetadataEntry> metadataTable;

    @FXML
    public TreeTableColumn<XMLTreeNode, String> metadataTableKeyCol;
    //public TreeTableColumn<MetadataEntry, String> metadataTableKeyCol;
    @FXML
    public TreeTableColumn<XMLTreeNode, String> metadataTableValueCol;
    //public TreeTableColumn<MetadataEntry, String> metadataTableValueCol;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String directoryPath = "src/images";
        File directory = new File(directoryPath);

        populateFileDirectoryTreeView(directory);

        File file = new File("src/images/sample.tif");
        MetadataDecoder metadataDecoder = MetadataDecoderFactory.createDecoder(file);
        assert metadataDecoder != null;
        XMLNode xmlRootNode = gdal.ParseXMLString(metadataDecoder.getSpatialReferenceXML());
        TreeItem<XMLTreeNode> topNode = new TreeItem<>(new XMLTreeNode("Metadata", "", ""));
        TreeItem<XMLTreeNode> rootNode = new TreeItem<>(new XMLTreeNode("Spatial Reference Metadata", "", ""));
        TreeItem<XMLTreeNode> spatialRefRootNode = convertXMLNodeToTreeItem(xmlRootNode, "");

        Vector<String> metadataDomains = metadataDecoder.getMetadataDomains();
        TreeItem<XMLTreeNode> metadataRootNode = new TreeItem<>(new XMLTreeNode("Metadata Domains", "", ""));
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
        rootNode.setExpanded(true);
        spatialRefRootNode.setExpanded(true);
        metadataRootNode.setExpanded(true);
        rootNode.getChildren().add(spatialRefRootNode);
        topNode.getChildren().add(rootNode);
        topNode.getChildren().add(metadataRootNode);

        metadataTable.setRoot(topNode);
        metadataTable.setShowRoot(false);

        metadataTableKeyCol.setCellValueFactory(param -> param.getValue().getValue().nodeNameProperty());
        metadataTableValueCol.setCellValueFactory(param -> param.getValue().getValue().nodeValueProperty());
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
            nodeName = "EPSG";
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
                System.out.println(cell.getText());
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

}