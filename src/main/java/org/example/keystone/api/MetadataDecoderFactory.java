package org.example.keystone.api;

import java.io.File;

public class MetadataDecoderFactory {
    public static MetadataDecoder createDecoder(File file) {

        /*
            Factory class for creating new MetadataDecoder objects based on file type
         */
        String fileType = getFileExtension(file);

        if (fileType == null) {
            throw new IllegalArgumentException("File type cannot be null");
        }

        return switch (fileType) {
            case "tif", "TIF" -> new TiffDecoder(file);
            case "ntf" -> new NitfDecoder(file);
            case "jpg" -> null;
            case "png" -> null;
            default -> throw new UnsupportedOperationException("Unsupported File Type: " + fileType);
        };
    }

    public static String getFileExtension(File file) {
        // takes in a File object and parses the file name to obtain the file extension
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');

        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return null;
        }

        return fileName.substring(lastDotIndex + 1);
    }
}
