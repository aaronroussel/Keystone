package org.example.keystone.api;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.geotools.coverage.grid.io.imageio.IIOMetadataDumper;
import org.geotools.coverage.grid.io.imageio.geotiff.GeoTiffIIOMetadataDecoder;
import org.geotools.gce.geotiff.GeoTiffReader;

import javax.imageio.metadata.IIOMetadataNode;
import java.io.File;
import java.util.Vector;

public class TiffMetadataDecoder implements ImageProcessor {

    public String getMetadata(File tiffFile) {

        /*
            Geotools will throw an error if the file is a regular tiff file, so it MUST be a GeoTiff file.
            need to add appropriate handling for the case of a regular tiff file.
         */
        GeoTiffReader reader;
        try {
            reader = new GeoTiffReader(tiffFile);
            GeoTiffIIOMetadataDecoder metadata = reader.getMetadata();
            IIOMetadataNode mdNode = metadata.getRootNode();
            IIOMetadataDumper mdDump = new IIOMetadataDumper(mdNode);
            return mdDump.getMetadata();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Vector<String> getPrivateMetadata(File tiffFile) {
        gdal.AllRegister();

        String filePath = tiffFile.getPath();

        Dataset dataset = gdal.Open(filePath);
        // Open the dataset in read-only mode
        if (dataset == null) {
            System.err.println("Error: Could not open dataset " + filePath);
            System.exit(1);
        }

        // Retrieve metadata. we can input a string to retrieve the value for a specific domain
        // ex dataset.GetMetadata_List("AREA_OR_POINT")
        // This function, as well as many others from GDAL and GeoTools, will return a raw vector with no type specified
        // we have to check if the value is null, then we can index through the vector. all elements will be of a
        // generic "Object" type and must be converted to the type we want
        Vector metadataDomains = dataset.GetMetadata_List();
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
        dataset.delete();
        return null;
    }
}
