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

    private Dataset dataset;
    private final File tiffFile;

    TiffMetadataDecoder(File tiffFile) {
        this.tiffFile = tiffFile;
        this.dataset = getDataset();
    }

    public String getMetadata() {

        /*
            This function returns all metadata fields EXCEPT private metadata fields. The returned string is in XML format

            Geotools will throw an error if the file is a regular tiff file, so it MUST be a GeoTiff file.
            need to add appropriate handling for the case of a regular tiff file.
         */
        GeoTiffReader reader;
        try {
            reader = new GeoTiffReader(this.tiffFile);
            GeoTiffIIOMetadataDecoder metadata = reader.getMetadata();
            IIOMetadataNode mdNode = metadata.getRootNode();
            IIOMetadataDumper mdDump = new IIOMetadataDumper(mdNode);
            return mdDump.getMetadata();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Vector<String> getPrivateMetadata() {

        /*
            This function returns ONLY private metadata fields. There are other functions within GDAL that are used
            to obtain metadata fields that are defined by the Geotiff standard. Currently we can use the getMetadata function
            above to obtain those fields, excluding the private fields obtained by this function.
         */

        if (this.dataset == null) {
            System.err.println("Error: Could not open dataset " + this.tiffFile.getPath());
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
        // used by the constructor to obtain a dataset from the input file object

        String filePath = this.tiffFile.getPath();
        gdal.AllRegister();

        Dataset dataset = gdal.Open(filePath);

        return dataset;
    }

    public void closeDataset() {
        // close the dataset to free resource. Java GC should handle freeing the resource once the object drops
        // out of scope but this may or may not be useful for something later so im going to provide acess to
        // this functionality just in case

        this.dataset.Close();
    }

    public boolean hasDataset() {
        // checks if the dataset is null or not. the only reason it would be null is if the closeDataset()
        // function is called and the object hasn't been cleaned up by GC yet.

        return this.dataset != null;
    }
}
