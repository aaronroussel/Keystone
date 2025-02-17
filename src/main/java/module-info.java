module org.example.keystone {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires org.geotools.geotiff;
    requires org.geotools.coverage;
    requires org.pf4j;
    requires org.slf4j;
    requires gdal;

    opens org.example.keystone to javafx.fxml;
    exports org.example.keystone;
    exports org.example.keystone.api;
    opens org.example.keystone.api to javafx.fxml;
}