package org.example.keystone.api;

import org.gdal.gdal.Dataset;
import org.gdal.osr.SpatialReference;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

public class TiffDecoder extends MetadataDecoder {

    public TiffDecoder(File file, Dataset dataset) {
        super(file, dataset);
    }

    public void setSpatialReferenceFromWKT(String wktString) {
        /*
            Takes a string in WKT format and creates a new spatial reference then writes it to the dataset. This can
            be used to write new geo-referencing data.
         */
        SpatialReference spatialReference = new SpatialReference(wktString);
        super.dataset.SetSpatialRef(spatialReference);
    }

    public void setSpatialReference(SpatialReference srs) {
        super.dataset.SetSpatialRef(srs);
    }

    public void setSpatialReferenceFromXML(String xml) {
        /*
            Creates a new spatial reference using XML
         */
        SpatialReference spatialReference = new SpatialReference();
        spatialReference.ImportFromXML(xml);
    }

    public void setMetadataField(String key, String value) {
        /*
            Writes a single key-value pair to the file metadata under the default metadata domain
         */

        this.dataset.SetMetadataItem(key.toUpperCase(), value);
        this.save();
    }

    public void setMetadataField(String key, String value, String domain) {
        /*
            Writes a single key-value pair to the specified metadata domain
         */
        this.dataset.SetMetadataItem(key.toUpperCase(), value, domain);
    }

    public void setMetadataFromHashTable(Hashtable<String, String> metadataHashTable, String domain) {

        /*
            Takes a Hashtable and writes all entries to the specified metadata domain. Using this we can write multiple
            metadata entries at once. If a key already exists, the value will be overwritten with the new value. keys
            that don't exist will be added to the metadata domain.
         */

        Hashtable<String, String> tempHashTable = new Hashtable<>();
        /*
            Loop through the entire hashtable and change the keys to uppercase, then adds to a new Hashtable.
            it ain't pretty but it works.
         */
        Enumeration<String> keys = metadataHashTable.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String uppercaseKey = key.toUpperCase();
            String value = metadataHashTable.get(key);
            tempHashTable.put(uppercaseKey, value);
        }

        this.dataset.SetMetadata(tempHashTable, domain);
    }

    public void setMetadataFromHashTable(Hashtable<String, String> metadataHashTable) {

        /*
            No domain input for this version of the function, so it defaults to writing to the default metadata domain
         */

        Hashtable<String, String> tempHashTable = new Hashtable<>();

        Enumeration<String> keys = metadataHashTable.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String uppercaseKey = key.toUpperCase();
            String value = metadataHashTable.get(key);
            tempHashTable.put(uppercaseKey, value);
        }

        this.dataset.SetMetadata(tempHashTable);
    }
}
