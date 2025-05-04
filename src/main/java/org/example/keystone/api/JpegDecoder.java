
package org.example.keystone.api;

import org.apache.commons.imaging.*;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.gdal.gdal.Dataset;
import org.gdal.osr.SpatialReference;

import java.io.*;
import java.util.Hashtable;

public class JpegDecoder extends MetadataDecoder {

    public JpegDecoder(File file, Dataset dataset) {
        super(file, dataset);
    }

    @Override
    public void setSpatialReference(SpatialReference srs) {
        try {
            double[] latLon = getCenterLatLon();
            writeGPSWithExifTool(latLon[0], latLon[1]);
            writeXmpWithExifTool("WKT_SRS", srs.ExportToPrettyWkt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSpatialReferenceFromWKT(String wktString) {
        writeXmpWithExifTool("WKT_SRS", wktString);
    }

    @Override
    public void setMetadataField(String key, String value) {
        setXmpField(key, value);
    }

    @Override
    public void setMetadataField(String key, String value, String domain) {
        setXmpField(key, value);
    }

    @Override
    public void setMetadataFromHashTable(Hashtable<String, String> metadataHashTable) {
        for (String key : metadataHashTable.keySet()) {
            setMetadataField(key, metadataHashTable.get(key));
        }
    }

    @Override
    public void setMetadataFromHashTable(Hashtable<String, String> metadataHashTable, String domain) {
        setMetadataFromHashTable(metadataHashTable);
    }

    private void setXmpField(String key, String value) {
        writeXmpWithExifTool(key, value);
    }

    private void writeGPSWithExifTool(double lat, double lon) throws IOException {
        String latRef = lat >= 0 ? "N" : "S";
        String lonRef = lon >= 0 ? "E" : "W";
        ProcessBuilder pb = new ProcessBuilder(
                "exiftool",
                "-overwrite_original",
                "-GPSLatitude=" + Math.abs(lat),
                "-GPSLatitudeRef=" + latRef,
                "-GPSLongitude=" + Math.abs(lon),
                "-GPSLongitudeRef=" + lonRef,
                file.getAbsolutePath()
        );
        pb.inheritIO().start();
    }

    private void writeXmpWithExifTool(String key, String value) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "exiftool",
                    "-overwrite_original",
                    "-XMP-dc:" + key + "=" + value,
                    file.getAbsolutePath()
            );
            pb.inheritIO().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double[] getCenterLatLon() {
        Hashtable<String, String> coords = getCornerCoordinates();
        String[] center = coords.get("Center").replace("(", "").replace(")", "").split(", ");
        return new double[] { Double.parseDouble(center[1]), Double.parseDouble(center[0]) }; // lat, lon
    }
}
