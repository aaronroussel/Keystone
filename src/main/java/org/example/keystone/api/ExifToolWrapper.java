package org.example.keystone.api;

import java.io.File;
import java.io.IOException;

public class ExifToolWrapper {
    public static void writeGPSMetadata(File file, double lat, double lon) throws IOException {
        String latRef = lat >= 0 ? "N" : "S";
        String lonRef = lon >= 0 ? "E" : "W";

        ProcessBuilder pb = new ProcessBuilder(
                "exiftool",
                "-GPSLatitude=" + Math.abs(lat),
                "-GPSLatitudeRef=" + latRef,
                "-GPSLongitude=" + Math.abs(lon),
                "-GPSLongitudeRef=" + lonRef,
                file.getAbsolutePath()
        );
        pb.inheritIO().start();
    }
}