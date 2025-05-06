package org.example.keystone.api;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.InfoOptions;
import org.gdal.gdal.gdal;
import org.gdal.osr.SpatialReference;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;


public abstract class MetadataDecoder {
    // parent class for metadata decoders. decoders for specific file types should inherit this class.
    // should help reduce amount of repeated code.


    /*
        This will probably need to be completed and implemented AFTER we have a working base implementation for
        reading meta for each file type we are supporting, then once that is completed we can refactor into something
        that is structured better. The gdal functions operate basically the same for NITF and GeoTIFF, and may do so
        for other supported file types as well. It would be better to have the shared logic defined in a parent class
        to reduce the amount of repeated code.

        This class should also probably implement some kind of interface with generalized function definitions for common
        operations. This will tie in with pf4j for the plugin support. We have reading and writing metadata as core functionality
        and plugins will implement the specifics. This parent class should also implement that interface once it's written. Again,
        this will probably be done AFTER we have working implementations for all of our supported file types, that way we have a
        big picture look at everything and can refactor from there into something with better structure than isolated classes with
        a lot of repeated code
     */


    //                              ----------------------WORK IN PROGRESS--------------------------
    protected Dataset dataset;
    protected File file;


    MetadataDecoder(File file, Dataset dataset) {

        if (!file.isFile()) {
            throw new IllegalArgumentException("Cannot instantiate object: " + file.getName() + " is not a valid file");
        }

        this.file = file;
        this.dataset = dataset;
    }

    public Vector<String> getMetadata() {

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
        Vector<String> metadataList = new Vector<>();

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

    public Vector<String> getMetadata(String domain) {

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



    public void getDataset() {
        // used by the constructor to obtain a dataset from the input file object

        String filePath = this.file.getAbsolutePath();
        gdal.AllRegister();
        
        if (filePath.isEmpty()) {
          throw new IllegalArgumentException("file path is not valid: " + filePath);
        }

        this.dataset = gdal.Open(filePath);
    }

    public Vector<String> getMetadataDomains() {
        /*
            this function gets us a list of all the metadata domains. this should be used to
            obtain metadata domains that can then be used as input for the dataset.GetMetadata_List() function
            in order to fetch the metadata contained in each specific domain
        */
        if (!this.hasDataset()) {
          throw new IllegalArgumentException("No valid dataset associated with this object");
        }

        Vector rawMetadataDomains = this.dataset.GetMetadataDomainList();
        Vector<String> metadataDomains = new Vector<>();
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

        if (!this.hasDataset()) {
            throw new IllegalArgumentException("No valid dataset associated with this object");
        }

        Hashtable rawMetadataTable = this.dataset.GetMetadata_Dict();
        Hashtable<String, String> metadataTable = new Hashtable<>();
        for (Object keyObject : rawMetadataTable.keySet()) {
            Object valueObject = rawMetadataTable.get(keyObject);
            metadataTable.put(keyObject.toString(), valueObject.toString());
        }

        return metadataTable;
    }

    public Hashtable<String, String> getMetadataHashTable(String domain) {

        Hashtable rawMetadataTable = this.dataset.GetMetadata_Dict(domain);
        Hashtable<String, String> metadataTable = new Hashtable<>();
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
        if (!this.hasDataset()) {
            throw new IllegalArgumentException("No valid dataset associated with this object");
        }

        String projection = this.dataset.GetProjection();

        if (projection == null || projection.isEmpty()) {
            throw new IllegalArgumentException("null or empty projection found for this dataset");
        }

        return new SpatialReference(projection);
    }

    public String getSpatialReferenceWKT() {
        /*
            This function returns a String in WKT format for the spatial reference data contained in the dataset
            For more info on WKT Format see: https://libgeos.org/specifications/wkt/
         */
        if (!this.hasDataset()) {
            throw new IllegalArgumentException("No valid dataset associated with this object");
        }

        SpatialReference spatialReference = getSpatialReference();
        String spatialString;

        if (spatialReference.IsProjected() == 1 || spatialReference.IsGeographic() == 1 || spatialReference.IsGeocentric() == 1) {
            spatialString = spatialReference.ExportToPrettyWkt();
        } else {
            spatialString = "Spatial Reference is null";
        }

        return spatialString;
    }

    public String getSpatialReferenceXML() {
        if (!this.hasDataset()) {
            throw new IllegalArgumentException("No valid dataset associated with this object");
        }

        SpatialReference spatialReference = getSpatialReference();
        return spatialReference.ExportToXML();
    }

    public String getAllMetadataJSON() {

        /*
            Returns ALL metadata info for a dataset in JSON format. This contains everything we are concerned with as
            well as some information we are not concerned with.

            The information returned here is the exact same information given when using the gdalinfo CLI command. You
            can check this by running "gdalinfo {filename/path} -json" from the command line
         */

        Vector<String> options = new Vector<>();
        options.add("-json");
        InfoOptions infoOptions = new InfoOptions(options);
        return gdal.GDALInfo(this.dataset, infoOptions);
    }

    public void printAllMetadata() {
        Vector<String> options = new Vector<>();
        options.add("");
        InfoOptions infoOptions = new InfoOptions(options);
        String metadata = gdal.GDALInfo(this.dataset, infoOptions);
        System.out.println(metadata);
    }

    public static SpatialReference newSpatialReferenceFromWkt(String wkt) {
        
        if (wkt == null || wkt.isEmpty()) {
            throw new IllegalArgumentException("null or empty WKT string");
        }

        SpatialReference spatialReference = new SpatialReference();
        spatialReference.ImportFromWkt(wkt);
        return spatialReference;
    }

    public static SpatialReference newSpatialReferenceFromGCS(String gcs) {

        if (gcs == null || gcs.isEmpty()) {
            throw new IllegalArgumentException("null or empty GCS string");
        }

        SpatialReference spatialReference = new SpatialReference();
        spatialReference.SetWellKnownGeogCS(gcs);
        return spatialReference;
    }

    public static SpatialReference newSpatialReferenceFromEPSG(int epsg) {

        if (epsg < 1024 || epsg > 32767) {
            throw new IllegalArgumentException("Invalid EPSG code");
        }

        SpatialReference spatialReference = new SpatialReference();
        spatialReference.ImportFromEPSG(epsg);
        return spatialReference;
    }

    public void setUTM(int zone, int hemisphere, SpatialReference srs) {
        srs.SetUTM(zone, hemisphere);
    }

    public static SpatialReference newSpatialReferenceFromProj4(String proj) {
        
        if (proj == null || proj.isEmpty()) {
            throw new IllegalArgumentException("Proj string is null or empty");
        }

        SpatialReference spatialReference = new SpatialReference();
        spatialReference.ImportFromProj4(proj);
        return spatialReference;
    }

    public static SpatialReference newSpatialReferenceFromXML(String xml) {
        
        if (xml == null || xml.isEmpty()) {
            throw new IllegalArgumentException("XML string is null or empty");
        }

        SpatialReference spatialReference = new SpatialReference();
        spatialReference.ImportFromXML(xml);
        return spatialReference;
    }

    public Hashtable<String, String> getGeoTransform() {

        /*
            This function gets the affine transformation data from a dataset. for more info 
            on the data fields contained in affine transformation data, check this link: 
            https://gdal.org/en/stable/tutorials/geotransforms_tut.html
        */ 

        double[] geoTransform = new double[6];
        Hashtable<String, String> gt = new Hashtable<>();

        dataset.GetGeoTransform(geoTransform);
        if (checkIfValidGeoTransformArray(geoTransform)) {
            gt.put("Top Left X", Double.toString(geoTransform[0]));
            gt.put("Pixel Width", Double.toString(geoTransform[1]));
            gt.put("Rotation (X-axis)", Double.toString(geoTransform[2]));
            gt.put("Top Left Y", Double.toString(geoTransform[3]));
            gt.put("Rotation (Y-axis)", Double.toString(geoTransform[4]));
            gt.put("Pixel Height (negative for north-up)", Double.toString(geoTransform[5]));
        } else {
            throw new NullPointerException("No valid Geo-Transform for this dataset");
        }
         
        return gt;
    }

    public static boolean checkIfValidGeoTransformArray(double[] array) {

        /* an ugly helper function to check if a GeoTranform Array is valid. 
           a newly initialized array in java will have default values, in this case its 0.0
           so we need to check if the array is still filled with all default values. if it does,
           we can assume that there is no valid GeoTransform since the function didn't place any
           new data into the array.
        */

        return !(array[0] == 0.0 && array[1] == 0.0 && array[2] == 0.0
                            && array[3] == 0.0 && array[4] == 0.0 && array[5] == 0.0);
    }

    public Hashtable<String, String> getCornerCoordinates() {

        /*
         * This function gets the corner coordinates of the image if there is a valid GeoTransform associated
         * with the dataset. It now also includes the geographic coordinate of the image center.
         */
        Hashtable<String, String> cornerCoordinates = new Hashtable<>();
        double[] geoTransform = new double[6];

        // The GetGeoTransform function modifies an existing array in place
        dataset.GetGeoTransform(geoTransform);

        if (!checkIfValidGeoTransformArray(geoTransform)) {
            throw new NullPointerException("No valid Geo-Transform for this dataset, cannot get corner coordinates");
        }

        // We need the width and height of the raster to compute pixel coordinates.
        int width = dataset.getRasterXSize();
        int height = dataset.getRasterYSize();

        // Convert corner pixel locations to geographic coordinates
        double[] topLeft = pixelToGeo(geoTransform, 0, 0);
        double[] topRight = pixelToGeo(geoTransform, width, 0);
        double[] bottomLeft = pixelToGeo(geoTransform, 0, height);
        double[] bottomRight = pixelToGeo(geoTransform, width, height);

        // Compute center coordinate
        int centerX = width / 2;
        int centerY = height / 2;
        double[] center = pixelToGeo(geoTransform, centerX, centerY);

        cornerCoordinates.put("Top Left",     "(" + topLeft[0]     + ", " + topLeft[1]     + ")");
        cornerCoordinates.put("Top Right",    "(" + topRight[0]    + ", " + topRight[1]    + ")");
        cornerCoordinates.put("Bottom Left",  "(" + bottomLeft[0]  + ", " + bottomLeft[1]  + ")");
        cornerCoordinates.put("Bottom Right", "(" + bottomRight[0] + ", " + bottomRight[1] + ")");
        cornerCoordinates.put("Center",       "(" + center[0]      + ", " + center[1]      + ")");

        return cornerCoordinates;
    }
    private double[] pixelToGeo(double[] geoTransform, int pixelX, int pixelY) {

        // Helper function to convert geoTransform data to corner coordinates
        
        double geoX = geoTransform[0] + pixelX * geoTransform[1] + pixelY * geoTransform[2];
        double geoY = geoTransform[3] + pixelX * geoTransform[4] + pixelY * geoTransform[5];
        return new double[]{geoX, geoY};
    }

    public void save() {
        this.dataset.FlushCache();

        this.dataset.delete();

        this.getDataset();


    }


        


    /*
        below are abstract function declarations for writing metadata. How we write metadata and how we enforce
        naming conventions will differ for each file type, so we only define an abstract definition here and then
        implement the concrete function in the decoder classes that extend this abstract class
     */
    
    public abstract void setSpatialReference(SpatialReference srs);

    public void setSpatialReferenceFromWKT(String wktString, String newFilename) {
        try {
            // 1. Convert to GeoTIFF with a passed-in filename (no .tif check needed)
            File geotiff = new File(this.file.getParent(), newFilename);



            // 2. Reopen dataset for the new file
            Dataset data = ImageProcessor.convertToGeoTiff(this.file, geotiff);

            // 3. Set spatial reference
            SpatialReference srs = new SpatialReference(wktString);
            data.SetSpatialRef(srs);
            data.FlushCache();
            data.delete();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setSpatialReferenceFromEPSG(String EPSG, String newFilename) throws Exception {
        try {
            int code = Integer.parseInt(EPSG);
            File geotiff = new File(this.file.getParent(), newFilename);
            Dataset data = ImageProcessor.convertToGeoTiff(this.file, geotiff);
            SpatialReference srs = newSpatialReferenceFromEPSG(code);
            data.SetSpatialRef(srs);
            data.FlushCache();
            data.delete();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }



    public abstract void setMetadataField(String key, String value);

    public abstract void setMetadataField(String key, String value, String domain);

    public abstract void setMetadataFromHashTable(Hashtable<String, String> metadataHashTable);

    public abstract void setMetadataFromHashTable(Hashtable<String, String> metadataHashTable, String domain);

    public boolean hasDataset() {
        return this.dataset != null;
    }

    public void closeDataset() {
        this.dataset.FlushCache();
        this.dataset.delete();
    }
}
