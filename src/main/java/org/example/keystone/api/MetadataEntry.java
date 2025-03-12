package org.example.keystone.api;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MetadataEntry {
    private final StringProperty key;
    private final StringProperty value;

    public MetadataEntry(String key, String value) {
        this.key = new SimpleStringProperty(key);
        this.value = new SimpleStringProperty(value);
    }

    public StringProperty keyProperty() { return key; }
    public StringProperty valueProperty() { return value; }
}
