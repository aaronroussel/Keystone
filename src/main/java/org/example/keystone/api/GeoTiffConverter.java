package org.example.keystone.api;

import org.gdal.gdal.*;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.osr.SpatialReference;
import java.io.File;

public class GeoTiffConverter {

    static {
        gdal.AllRegister(); // Initialize GDAL once
    }

    /**
     * Converts any GDAL-supported file to GeoTIFF
     * @param inputFile Source file (NITF, JPEG, etc.)
     * @param outputFile Destination GeoTIFF path
     * @throws Exception If conversion fails
     */
    public static void convertToGeoTiff(File inputFile, File outputFile) throws Exception {
        Dataset srcDs = gdal.Open(inputFile.getAbsolutePath());
        if (srcDs == null) {
            throw new Exception("GDAL failed to open: " + gdal.GetLastErrorMsg());
        }

        Driver driver = gdal.GetDriverByName("GTiff");
        Dataset dstDs = driver.CreateCopy(
                outputFile.getAbsolutePath(),
                srcDs,
                0, // Strict copy
                new String[] { "TILED=YES", "COMPRESS=LZW" } // GeoTIFF options
        );

        if (dstDs == null) {
            srcDs.delete();
            throw new Exception("Conversion failed: " + gdal.GetLastErrorMsg());
        }

        dstDs.delete();
        srcDs.delete();
    }

    /**
     * Writes spatial reference to an existing GeoTIFF
     * @param geoTiffFile Target GeoTIFF file
     * @param srs SpatialReference object (EPSG, WKT, etc.)
     */
    public static void writeSpatialReference(File geoTiffFile, SpatialReference srs) throws Exception {
        Dataset ds = gdal.Open(geoTiffFile.getAbsolutePath(), gdalconstConstants.GA_Update);
        ds.SetProjection(srs.ExportToWkt());

        // Force write geotransform if needed
        double[] gt = ds.GetGeoTransform();
        ds.SetGeoTransform(gt);

        ds.delete(); // Saves changes
    }
}