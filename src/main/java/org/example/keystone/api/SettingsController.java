package org.example.keystone.api;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SettingsController {
    @FXML
    TextField cacheSizeField;

    @FXML
    TextField subsampleSizeField;

    @FXML
    public void initialize() {
        cacheSizeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                cacheSizeField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        subsampleSizeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                subsampleSizeField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
    }
}
