package org.example.keystone.api;

import java.io.File;

public class MetadataDecoderFactory {
    public static MetadataDecoder createDecoder(File file) {
        String fileType = getFileExtension(file);

        if (fileType == null) {
            throw new IllegalArgumentException("File type cannot be null");
        }

        return switch (fileType) {
            case "tif" -> new TiffDecoder(file);
            case "ntf" -> new NitfDecoder(file);
            case "jpg" -> null;
            case "png" -> null;
            default -> throw new UnsupportedOperationException("Unsupported File Type: " + fileType);
        };
    }

    public static String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');

        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return null;
        }

        return fileName.substring(lastDotIndex + 1);
    }
}
