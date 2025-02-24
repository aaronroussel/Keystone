package org.example.keystone.api;

import org.gdal.osr.SpatialReference;

import java.io.File;

public class TiffDecoder extends MetadataDecoder {

    public TiffDecoder(File file) {
        super(file);
        super.getDataset();
    }

    public void setSpatialReferenceFromWKT(String wktString) {
        SpatialReference spatialReference = new SpatialReference(wktString);
        super.dataset.SetSpatialRef(spatialReference);
    }

    public void setMetadataField(String key, String value) {
        this.dataset.SetMetadataItem(key.toUpperCase(), value);
    }

    public void setMetadataField(String key, String value, String domain) {
        this.dataset.SetMetadataItem(key.toUpperCase(), value, domain);
    }
}
