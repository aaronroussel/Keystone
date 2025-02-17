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

        /*
            This function returns ONLY private metadata fields. There are other functions within GDAL that are used
            to obtain metadata fields that are defined by the Geotiff standard. Currently we can use the getMetadata function
            above to obtain those fields, excluding the private fields obtained by this function.
         */

        if (this.dataset == null) {
            System.err.println("Error: Could not open dataset " + this.nitfFile.getPath());
            System.exit(1);
        }

        // Retrieve metadata. we can input a string to retrieve the value for a specific domain
        // ex dataset.GetMetadata_List("AREA_OR_POINT")
        // This function, as well as many others from GDAL and GeoTools, will return a raw vector with no type specified
        // we have to check if the value is null, then we can index through the vector. all elements will be of a
        // generic "Object" type and must be converted to the type we want
        Vector metadataDomains = this.dataset.GetMetadata_List();

        Vector<String> metaDataStrings = new Vector<String>();
        if (metadataDomains != null) {
            // Loop through all elements of the vector
            for (Object element : metadataDomains) {
                // Check if element is not null to avoid NullPointerException
                if (element instanceof String) {
                    metaDataStrings.add(element.toString());
                } else {
                    System.out.println("null");
                }
            }

            return metaDataStrings;
        } else {
            System.out.println("The returned Vector is null.");
        }
        // Close the dataset to free resources
        return null;
    }

    private Dataset getDataset() {
        gdal.AllRegister();
        return gdal.Open(this.nitfFile.getPath());
    }
}
