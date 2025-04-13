package org.example.keystone.api;

import com.sun.source.tree.Tree;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import org.gdal.gdal.XMLNode;
import org.gdal.gdal.gdal;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

public class editMetadataTreeBuilder {

    public static void buildTree(String filePath, TreeTableView<XMLTreeNode> metadataTable, TreeTableColumn<XMLTreeNode, String> metadataTableKeyCol, TreeTableColumn<XMLTreeNode, String> metadataTableValueCol) {
            try {
                File file = new File(filePath);
                MetadataDecoder metadataDecoder = MetadataDecoderFactory.createDecoder(file);

                if (metadataDecoder == null) {
                    throw new IllegalArgumentException("Unsupported file format, or unable to create decoder");
                }

                TreeItem<XMLTreeNode> topNode = new TreeItem<>(new XMLTreeNode("Metadata", "", ""));
                TreeItem<XMLTreeNode> rootNode = new TreeItem<>(new XMLTreeNode("Spatial Reference Metadata", "", ""));

                TreeItem<XMLTreeNode> spatialRefRootNode = getSpatialReferenceNode(metadataDecoder);
                TreeItem<XMLTreeNode> geoTransformRootNode = getGeoTransformNode(metadataDecoder);
                TreeItem<XMLTreeNode> cornerCoordinatesRootNode = getCornerCoordinatesNode(metadataDecoder);
                TreeItem<XMLTreeNode> metadataRootNode = getMetadataDomainNode(metadataDecoder);


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


                // code to turn value column into text fields and make them editable
                metadataTable.setEditable(true);

                metadataTableValueCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());

                metadataTableValueCol.setOnEditCommit(event -> {
                    TreeItem <XMLTreeNode> currentEditingMetadata = metadataTable.getTreeItem(event.getTreeTablePosition().getRow());

                    XMLTreeNode treeNode = currentEditingMetadata.getValue();

                    treeNode.setNodeValue(event.getNewValue());

                    String metadataKey = treeNode.nodeNameProperty().get();
                    String metadataValue = treeNode.nodeValueProperty().get();


                    String extension = "";

                    int i = filePath.lastIndexOf('.');
                    if (i > 0) {
                        extension = filePath.substring(i+1);
                    }

                    System.out.println(extension);

                    switch (extension) {
                        case "tif":
                            TiffDecoder tiffDecoder = new TiffDecoder(file, metadataDecoder.dataset);

                            tiffDecoder.setMetadataField(metadataKey, metadataValue);
                            //tiffDecoder.setMetadataFromHashTable(metadataDecoder.getMetadataHashTable(domain););
                            break;
                        case "ntf":
                            NitfDecoder ntfDecoder = new NitfDecoder(file, metadataDecoder.dataset);

                            ntfDecoder.setMetadataField(metadataKey, metadataValue);
                            break;
                        case "jpeg":
                            JpegDecoder jpegDecoder = new JpegDecoder(file, metadataDecoder.dataset);

                            jpegDecoder.setMetadataField(metadataKey, metadataValue);
                            break;
                    }


                });





            } catch (Exception e) {
                throw new RuntimeException(e);
            }

    }

    private static TreeItem<XMLTreeNode> getSpatialReferenceNode(MetadataDecoder metadataDecoder) {
        // Spatial Ref

        TreeItem<XMLTreeNode> spatialRefRootNode = null;

        try {
            String spatialReferenceXML = metadataDecoder.getSpatialReferenceXML();

            if (spatialReferenceXML != null && !spatialReferenceXML.isEmpty())  {
                XMLNode xmlRootNode = gdal.ParseXMLString(spatialReferenceXML);
                spatialRefRootNode = convertXMLNodeToTreeItem(xmlRootNode, "");
            }

        } catch (Exception e) {
            System.err.println("Failed to retrieve spatial reference metadata: does not exist");
        }

        return spatialRefRootNode;
    }

    private static TreeItem<XMLTreeNode> getMetadataDomainNode(MetadataDecoder metadataDecoder) {


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

        return metadataRootNode;
    }

    private static TreeItem<XMLTreeNode> getGeoTransformNode(MetadataDecoder metadataDecoder) {

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

        return geoTransformRootNode;
    }

    private static TreeItem<XMLTreeNode> getCornerCoordinatesNode(MetadataDecoder metadataDecoder) {

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

        return cornerCoordinatesRootNode;
    }


    private static Vector<TreeItem<XMLTreeNode>> convertHashTableToTreeEntries(Hashtable<String, String> table) {
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

    private static TreeItem<XMLTreeNode> convertXMLNodeToTreeItem(XMLNode node, String parentPath) {

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

}
