package org.example.keystone.api;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.geotools.coverage.grid.io.imageio.IIOMetadataDumper;
import org.geotools.coverage.grid.io.imageio.geotiff.GeoTiffIIOMetadataDecoder;
import org.geotools.gce.geotiff.GeoTiffReader;

import javax.imageio.metadata.IIOMetadataNode;
import java.io.File;
import java.util.Vector;

public class TiffMetadataDecoder implements MetadataProcessor {

    private Dataset dataset;
    private final File tiffFile;

    public TiffMetadataDecoder(File tiffFile) {
        this.tiffFile = tiffFile;
        this.dataset = getDataset();
    }

    public String getMetadata() {

        /*
            This function returns all metadata fields EXCEPT private metadata fields. The returned string is in XML format

            Geo tools will throw an error if the file is a regular tiff file, so it MUST be a GeoTiff file.
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

        if (this.dataset == null) {
            System.err.println("Error: Could not open dataset " + this.tiffFile.getPath());
            System.exit(1);
        }

        // Retrieve metadata. we can input a string to retrieve the value for a specific domain.
        // ex dataset.GetMetadata_List("AREA_OR_POINT")
        // if no input is provided, the function will return only metadata contained in the default domain
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

    private Dataset getDataset() {
        // used by the constructor to obtain a dataset from the input file object

        String filePath = this.tiffFile.getPath();
        gdal.AllRegister();

        Dataset dataset = gdal.Open(filePath);

        return dataset;
    }

    public Vector<String> getMetadataDomains() {
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

    public boolean hasDataset() {
        // checks if the dataset is null or not. the only reason it would be null is if the closeDataset()
        // function is called and the object hasn't been cleaned up by GC yet.

        return this.dataset != null;
    }
}
