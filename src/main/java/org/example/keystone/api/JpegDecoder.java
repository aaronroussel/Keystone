package org.example.keystone.api;

import org.gdal.gdal.Dataset;
import org.gdal.osr.SpatialReference;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

public class JpegDecoder extends MetadataDecoder {

    public JpegDecoder(File file, Dataset dataset) {
        super(file, dataset);
    }

    public void setSpatialReferenceFromWKT(String wktString) {
        /*
            Takes a string in WKT format and creates a new spatial reference then writes it to the dataset.
            This can be used to write new geo-referencing data.
        */
        SpatialReference spatialReference = new SpatialReference(wktString);
        super.dataset.SetSpatialRef(spatialReference);
    }

    public void setSpatialReference(SpatialReference srs) {
        this.dataset.SetSpatialRef(srs);
    }

    public void setMetadataField(String key, String value) {
        /*
            Writes a single key-value pair to the file metadata under the default metadata domain.
            For JPEG files, this would typically be EXIF metadata.
        */
        String jpegKey = "EXIF_" + key.toUpperCase();
        this.dataset.SetMetadataItem(jpegKey, value);
    }

    public void setMetadataField(String key, String value, String domain) {
        /*
            Writes a single key-value pair to the specified metadata domain.
            Common domains for JPEG include "EXIF", "IPTC", and "XMP".
        */
        String jpegKey = domain + "_" + key.toUpperCase();
        this.dataset.SetMetadataItem(jpegKey, value, domain);
    }

    public void setMetadataFromHashTable(Hashtable<String, String> metadataHashTable) {
        /*
            Sets multiple metadata fields at once in the default (EXIF) domain.
        */
        Hashtable<String, String> tempHashTable = new Hashtable<>();

        Enumeration<String> keys = metadataHashTable.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String jpegKey = "EXIF_" + key.toUpperCase();
            String value = metadataHashTable.get(key);
            tempHashTable.put(jpegKey, value);
        }

        this.dataset.SetMetadata(tempHashTable);
    }

    public void setExifMetadata(Hashtable<String, String> exifData) {
        /*
            Specialized method for setting EXIF metadata, which is commonly used in JPEG files.
        */
        setMetadataFromHashTable(exifData, "EXIF");
    }

    public void setMetadataFromHashTable(Hashtable<String, String> metadataHashTable, String domain) {
        /*
            Sets multiple metadata fields at once in the specified domain.
        */
        Hashtable<String, String> tempHashTable = new Hashtable<>();

        Enumeration<String> keys = metadataHashTable.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String jpegKey = domain + "_" + key.toUpperCase();
            String value = metadataHashTable.get(key);
            tempHashTable.put(jpegKey, value);
        }

        this.dataset.SetMetadata(tempHashTable, domain);
    }

}