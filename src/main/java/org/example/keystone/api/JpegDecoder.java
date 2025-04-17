package org.example.keystone.api;

import org.gdal.gdal.Dataset;
import org.gdal.osr.SpatialReference;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

public class JpegDecoder extends MetadataDecoder {
    JpegDecoder(File file, Dataset dataset, ImageProcessor imageprocessor) {
        super(file, dataset, imageprocessor);
    }

    @Override
    public void setSpatialReference(SpatialReference srs) {

    }

    @Override
    public void setSpatialReferenceFromWKT(String wktString) {

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

    @Override
    public Vector<String> getMetadataDomains() {
        Vector domainsRaw = super.dataset.GetMetadataDomainList();
        Vector<String> domains = new Vector<>();
        for (Object domain : domains) {
            domains.add(domain.toString());
        }
        domains.add("");
        domains.add("xml:XMP");

        return domains;
    }



}
