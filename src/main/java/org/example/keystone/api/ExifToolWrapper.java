
package org.example.keystone.api;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

public class ExifToolWrapper {

    public static void writeGPSMetadata(File file, double lat, double lon) throws IOException {
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

    public static void writeXmpMetadata(File file, String key, String value) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "exiftool",
                "-overwrite_original",
                "-XMP-dc:" + key + "=" + value,
                file.getAbsolutePath()
        );
        pb.inheritIO().start();
    }
}
