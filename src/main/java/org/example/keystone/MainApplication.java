package org.example.keystone;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.example.keystone.api.MetadataDecoder;
import org.example.keystone.api.MetadataDecoderFactory;
import org.gdal.gdal.gdal;
import org.gdal.ogr.ogr;
import org.gdal.osr.SpatialReference;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // ----------- Init Plugins ----------
        PluginManager pluginManager = new DefaultPluginManager();
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
        // ------------ GDAl -----------------
        gdal.AllRegister();
        // ----------- Init UI -----------
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("hello-view.fxml")); // load fxml file
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080); // create new scene with loaded fxml
        stage.setTitle("Keystone");
        scene.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());
        stage.setScene(scene);
        stage.show(); // display scene
    }

    public static void main(String[] args) {
        launch();
    }

}
