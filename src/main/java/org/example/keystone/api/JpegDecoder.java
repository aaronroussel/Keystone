
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
            ExifToolWrapper.writeGPSMetadata(file, latLon[0], latLon[1]);
            ExifToolWrapper.writeXmpMetadata(file, "WKT_SRS", srs.ExportToPrettyWkt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        try {
            ExifToolWrapper.writeXmpMetadata(file, key, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
