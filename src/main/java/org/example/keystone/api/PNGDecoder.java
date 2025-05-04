
package org.example.keystone.api;

import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.png.PngText;
import org.gdal.gdal.Dataset;
import org.gdal.osr.SpatialReference;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class PNGDecoder extends MetadataDecoder {

    public PNGDecoder(File file, Dataset dataset) {
        super(file, dataset);
    }

    @Override
    public void setSpatialReference(SpatialReference srs) {
        setMetadataField("WKT_SRS", srs.ExportToPrettyWkt());
    }

    @Override
    public void setSpatialReferenceFromWKT(String wktString) {
        setMetadataField("WKT_SRS", wktString);
    }

    @Override
    public void setMetadataField(String key, String value) {
        try {
            // Write XMP using ExifTool as a structured iTXt chunk
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

    @Override
    public void setMetadataField(String key, String value, String domain) {
        setMetadataField(key, value);
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
}
