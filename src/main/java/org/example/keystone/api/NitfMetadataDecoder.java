package org.example.keystone.api;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;

import java.io.File;
import java.util.Vector;

public class NitfMetadataDecoder {
    private final File nitfFile;
    private Dataset dataset;

    public NitfMetadataDecoder(File nitfFile) {
        this.nitfFile = nitfFile;
        this.dataset = getDataset();
    }

    public Vector<String> getPrivateMetadata() {

        if (this.dataset == null) {
            System.err.println("Error: Could not open dataset " + this.nitfFile.getPath());
            System.exit(1);
        }

        // Retrieve metadata. we can input a string to retrieve the value for a specific domain.
        // ex dataset.GetMetadata_List("AREA_OR_POINT")
        // if no input is provided, the GetMetadata_List() function will return only metadata contained in the default domain
        // This function, as well as many others from GDAL and GeoTools, will return a raw vector with no type specified
        // we have to check if the value is null, then we can index through the vector. all elements will be of a
        // generic "Object" type and must be converted to the type we want


        // using getMetadataDomains() we can obtain a list of all private metadata domains, which can then be used
        // to obtains the metadata contained in those domains by inputting each domain name into GetMetadata_List()
        Vector<String> metadataDomains = this.getMetadataDomains();
        Vector<String> metadataList = new Vector<String>();

        if (metadataDomains != null) {
            for (String s : metadataDomains) {
                Vector rawMetadata = this.dataset.GetMetadata_List(s);
                if (rawMetadata != null) {
                    for (Object object : rawMetadata) {
                        metadataList.add(object.toString());
                    }
                }
            }
        }

        return metadataList;
    }

    private Vector<String> getMetadataDomains() {
        // this function gets us a list of all the metadata domains. this should be used to
        // obtain metadata domains that can then be used as input for the dataset.GetMetadata_List() function
        // in order to fetch the metadata contained in each specific domain

        Vector rawMetadataDomains = this.dataset.GetMetadataDomainList();
        Vector<String> metadataDomains = new Vector<String>();
        if (rawMetadataDomains != null) {
            for (Object object : rawMetadataDomains) {
                metadataDomains.add(object.toString());
            }
            return metadataDomains;
        }

        return null;
    }

    private Dataset getDataset() {
        gdal.AllRegister();
        return gdal.Open(this.nitfFile.getPath());
    }

    private boolean hasDataset() {
        return this.dataset == null;
    }

}
