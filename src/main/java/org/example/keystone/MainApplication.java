package org.example.keystone;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.example.keystone.api.MetadataDecoder;
import org.example.keystone.api.MetadataDecoderFactory;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
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
        try {
            // 1. Verify GTiff driver
            Driver driver = gdal.GetDriverByName("GTiff");
            if (driver == null) {
                throw new Exception("GTiff driver not found. Check:\n" +
                        "1. GDAL_DATA environment variable\n" +
                        "2. Native libraries in java.library.path");
            }

            // 2. Create test file
            String tempFilePath = System.getProperty("java.io.tmpdir") + "/gdal_test.tif";
            System.out.println("\nCreating test file: " + tempFilePath);

            Dataset dataset = driver.Create(tempFilePath, 100, 100, 1, gdalconst.GDT_Byte);
            if (dataset == null) {
                throw new Exception("File creation failed: " + gdal.GetLastErrorMsg());
            }

            // 3. Set spatial reference (WGS84 example)
            String wkt = "GEOGCS[\"WGS 84\","
                    + "DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563]],"
                    + "PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]]";

            SpatialReference srs = new SpatialReference(wkt);
            dataset.SetProjection(srs.ExportToWkt());
            dataset.FlushCache();

            // 4. Verify
            System.out.println("\nSpatial Reference Set To:");
            System.out.println(new SpatialReference(dataset.GetProjection()).ExportToPrettyWkt());

            // 5. Cleanup
            dataset.delete();
            System.out.println("\nGDAL test completed successfully");

        } catch (Exception e) {
            System.err.println("\n!!! GDAL TEST FAILED !!!");
            e.printStackTrace();
            System.err.println("\nTroubleshooting tips:");
            System.err.println("1. Verify GDAL binaries are installed");
            System.err.println("2. Set GDAL_DATA environment variable");
            System.err.println("3. Check java.library.path contains gdaljni");
        }



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
