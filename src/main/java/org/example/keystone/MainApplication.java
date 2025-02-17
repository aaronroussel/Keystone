package org.example.keystone;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.example.keystone.api.TiffMetadataDecoder;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // ----------- Init Plugins ----------
        PluginManager pluginManager = new DefaultPluginManager();
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
        // ------------ GDAl -----------------

        // ----------- Init UI -----------
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("hello-view.fxml")); // load fxml file
        Scene scene = new Scene(fxmlLoader.load(), 320, 240); // create new scene with loaded fxml
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show(); // display scene
    }

    public static void main(String[] args) {
        launch();
    }
}
