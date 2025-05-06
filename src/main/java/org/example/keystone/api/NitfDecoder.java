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

    public void setMetadataField(String key, String value) {
        String newKey = key.toUpperCase();

        if (!key.startsWith("NITF_")) {
            newKey = "NITF_" + newKey;
        }

        this.dataset.SetMetadataItem(newKey, value.toUpperCase());
        this.dataset.FlushCache();
        this.dataset.delete();
        this.dataset = null;
        this.getDataset();
    }

    public void setMetadataField(String key, String value, String domain) {
        String newKey = key.toUpperCase();

        if (!key.startsWith("NITF_")) {
            newKey = "NITF_" + newKey;
        }

        this.dataset.SetMetadataItem(newKey, value.toUpperCase(), domain);
        this.dataset.FlushCache();
        this.dataset.delete();
        this.dataset = null;
        this.getDataset();
    }

    public void setMetadataFromHashTable(Hashtable<String, String> metadataHashTable) {
        System.out.println("NULL");
    }

    public void setMetadataFromHashTable(Hashtable<String, String> metadataHashTable, String domain) {
        System.out.println("NULL");
    }

    public void setSpatialReference(SpatialReference srs) {
        this.dataset.SetSpatialRef(srs);
    }
}
