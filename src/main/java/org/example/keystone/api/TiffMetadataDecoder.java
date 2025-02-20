package org.example.keystone.api;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.osr.SpatialReference;
import org.geotools.api.referencing.crs.ProjectedCRS;
import org.geotools.coverage.grid.io.imageio.IIOMetadataDumper;
import org.geotools.coverage.grid.io.imageio.geotiff.GeoTiffIIOMetadataDecoder;
import org.geotools.gce.geotiff.GeoTiffReader;

import javax.imageio.metadata.IIOMetadataNode;
import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
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

        /*
            Retrieve metadata. we can input a string to retrieve the value for a specific domain.
            ex dataset.GetMetadata_List("AREA_OR_POINT")
            if no input is provided, the function will return only metadata contained in the default domain
            This function, as well as many others from GDAL and GeoTools, will return a raw vector with no type specified
            we have to check if the value is null, then we can index through the vector. all elements will be of a
            generic "Object" type and must be converted to the type we want
        */


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

    public Vector<String> getPrivateMetadata(String domain) {

        // use this function if you need to return metadata only for a specific domain

        Vector<String> metadataList = new Vector<>();

        Vector rawMetadata = this.dataset.GetMetadata_List(domain);
        if (rawMetadata != null) {
            for (Object object : rawMetadata) {
                metadataList.add(object.toString());
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
        /*
            this function gets us a list of all the metadata domains. this should be used to
            obtain metadata domains that can then be used as input for the dataset.GetMetadata_List() function
            in order to fetch the metadata contained in each specific domain
        */

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

    public Hashtable<String, String> getMetadataHashTable() {
        /*
            returns a hashtable of key value pairs for all metadata in the specified domain. GetMetadata_Dict() works
            the same as other GDAL metadata functions. a string input tells which metadata domain to fetch the data
            from, and if no domain is specified (or any empty string is used) then the function will return the metadata
            in the default domain.
        */

        Hashtable rawMetadataTable = this.dataset.GetMetadata_Dict();
        Hashtable<String, String> metadataTable = new Hashtable<String, String>();
        for (Object keyObject : rawMetadataTable.keySet()) {
            Object valueObject = rawMetadataTable.get(keyObject);
            metadataTable.put(keyObject.toString(), valueObject.toString());
        }

        return metadataTable;
    }

    private SpatialReference getSpatialReference() {
        /*
            returns a SpatialReference object, which we can then use to obtain coordinate information for the dataset
         */
        SpatialReference spatialReference = new SpatialReference(this.dataset.GetProjection());
        return spatialReference;
    }

    public String getSpatialReferenceWKT() {
        /*
            This function returns a String in WKT format for the spatial reference data contained in the dataset
            For more info on WKT Format see: https://libgeos.org/specifications/wkt/
         */

        SpatialReference spatialReference = getSpatialReference();
        String spatialString;
        if (spatialReference.IsProjected() == 1 || spatialReference.IsGeographic() == 1 || spatialReference.IsGeocentric() == 1) {
            spatialString = spatialReference.ExportToPrettyWkt();
        } else {
            spatialString = "Spatial Reference is null";
        }
        return spatialString;
    }

    public boolean hasDataset() {
        /*
            checks if the dataset is null or not
        */

        return this.dataset != null;
    }
}
