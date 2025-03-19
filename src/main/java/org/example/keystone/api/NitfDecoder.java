package org.example.keystone.api;

import org.gdal.gdal.Dataset;
import org.gdal.osr.SpatialReference;

import java.io.File;
import java.util.Hashtable;

public class NitfDecoder extends MetadataDecoder {

    NitfDecoder(File file, Dataset dataset) {
        super(file, dataset);
    }

    public void setSpatialReferenceFromWKT(String wktString) {
        SpatialReference spatialReference = new SpatialReference(wktString);
        this.dataset.SetSpatialRef(spatialReference);
    }

    public void setSpatialReference(SpatialReference srs) {
        this.dataset.SetSpatialRef(srs);
    }

    public void setMetadataField(String key, String value) {
        String newKey = "NITF_" + key.toUpperCase();
        this.dataset.SetMetadataItem(newKey, value.toUpperCase());
    }

    public void setMetadataField(String key, String value, String domain) {
        String newKey = "NITF_" + key.toUpperCase();
        this.dataset.SetMetadataItem(newKey, value.toUpperCase(), domain);
    }

    public void setMetadataFromHashTable(Hashtable<String, String> metadataHashTable) {
        System.out.println("NULL");
    }

    public void setExifMetadata(Hashtable<String, String> exifData) {
    }

    public void setMetadataFromHashTable(Hashtable<String, String> metadataHashTable, String domain) {
        System.out.println("NULL");
    }

}
