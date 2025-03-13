package org.example.keystone;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.example.keystone.api.MetadataDecoder;
import org.example.keystone.api.MetadataDecoderFactory;
import org.example.keystone.api.TiffDecoder;
import org.gdal.gdal.XMLNode;
import org.gdal.gdal.XMLNodeType;
import org.gdal.gdal.gdal;
import org.gdal.ogr.ogr;
import org.gdal.osr.SpatialReference;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // ----------- Init Plugins ----------
        PluginManager pluginManager = new DefaultPluginManager();
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
        // ------------ GDAl -----------------
        File file = new File("src/images/test/sample.tif");
        MetadataDecoder metadataDecoder = MetadataDecoderFactory.createDecoder(file);
        assert metadataDecoder != null;

        //String s = metadataDecoder.getSpatialReferenceWKT();
        //assert s != null;
        //System.out.println("Current Spatial Reference:");
        //System.out.println(s);
        //System.out.println("--------------------------------");
        //SpatialReference srs = MetadataDecoder.newSpatialReferenceFromEPSG(2045);
        //metadataDecoder.setSpatialReference(srs);
        //s = metadataDecoder.getSpatialReferenceWKT();
        //System.out.println("New Spatial Reference:");
        //System.out.println(s);
        // ----------- Init UI -----------
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("hello-view.fxml")); // load fxml file
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080); // create new scene with loaded fxml
        stage.setTitle("Keystone");
        stage.setScene(scene);
        stage.show(); // display scene
    }

    public static void main(String[] args) {
        launch();
    }

}
