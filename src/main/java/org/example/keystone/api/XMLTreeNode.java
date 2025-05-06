package org.example.keystone.api;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class XMLTreeNode {
    private final StringProperty nodeName;
    private final StringProperty nodeValue;
    private final String nodePath;

    public XMLTreeNode(String name, String value, String path) {
        this.nodeName = new SimpleStringProperty(name);
        this.nodeValue = new SimpleStringProperty(value);
        this.nodePath = path;
    }

    public StringProperty nodeNameProperty() {
        return nodeName;
    }

    public StringProperty nodeValueProperty() {
        return nodeValue;
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setNodeName(String nodeName) {
        this.nodeName.set(nodeName);
    }

    public void setNodeValue(String value) {this.nodeValue.set(value);}
}
