module org.example.keystone {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.geotools.geotiff;
    requires org.geotools.coverage;
    requires org.pf4j;
    requires org.slf4j;
    requires gdal;
    requires org.geotools.api;
    requires org.geotools.metadata;
    requires jdk.compiler;
    requires jai.core;
    requires javafx.swing;
    requires org.apache.commons.imaging;

    opens org.example.keystone to javafx.fxml;
    exports org.example.keystone;
    exports org.example.keystone.api;
    opens org.example.keystone.api to javafx.fxml;
}