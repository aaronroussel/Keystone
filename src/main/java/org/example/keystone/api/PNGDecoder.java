package org.example.keystone.api;

import org.gdal.gdal.Dataset;
import org.gdal.osr.SpatialReference;

import java.io.File;
import java.util.Hashtable;

public class PNGDecoder extends MetadataDecoder {

    public PNGDecoder(File file, Dataset dataset) {
        super(file, dataset);

    }

    @Override
    public void setSpatialReference(SpatialReference srs) {

    }



    @Override
    public void setMetadataField(String key, String value) {

    }

    @Override
    public void setMetadataField(String key, String value, String domain) {

    }

    @Override
    public void setMetadataFromHashTable(Hashtable<String, String> metadataHashTable) {

    }

    @Override
    public void setMetadataFromHashTable(Hashtable<String, String> metadataHashTable, String domain) {

    }
}
