package org.example.keystone.api;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;

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
            case "tif", "TIF" -> new TiffDecoder(file, getDataset(file, true));
            case "ntf" -> new NitfDecoder(file, getDataset(file, true));
            case "jpg" -> new JpegDecoder(file, getDataset(file, false));
            case "png" -> new PngDecoder(file, getDataset(file, true));
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

    private static Dataset getDataset(File file, boolean updateMode) {
        // used by the constructor to obtain a dataset from the input file object

        String filePath = file.getAbsolutePath();
        gdal.AllRegister();

        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("file path is not valid: " + filePath);
        }

        int mode = updateMode ? gdalconstConstants.GA_Update : gdalconstConstants.GA_ReadOnly;
        Dataset dataset = gdal.Open(filePath, mode);

        return dataset;
    }
}
