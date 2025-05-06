package org.example.keystone.api;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MetadataDecoderFactory {

    private static final Map<String, byte[]> FILE_SIGNATURES = new HashMap<>();

    static {
        // TIFF: II (0x49 0x49) or MM (0x4D 0x4D)
        FILE_SIGNATURES.put("tif", new byte[]{0x49, 0x49});
        FILE_SIGNATURES.put("tif_alt", new byte[]{0x4D, 0x4D});

        // NITF
        FILE_SIGNATURES.put("ntf", new byte[]{0x4E, 0x49, 0x54, 0x46});

        // JPEG
        FILE_SIGNATURES.put("jpg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF});

        // PNG
        FILE_SIGNATURES.put("png", new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47});
    }
    public static MetadataDecoder createDecoder(File file) {

        /*
            Factory class for creating new MetadataDecoder objects based on file type
         */
        String fileType = detectFileTypeBySignature(file);

        if (fileType == null) {
            fileType = getFileExtension(file);
        }

        if (fileType == null) {
            throw new IllegalArgumentException("Unable to determine file type for: " + file.getName());
        }

        return switch (fileType) {
            case "tif", "TIF" -> new TiffDecoder(file, getDataset(file, true));
            case "ntf" -> new NitfDecoder(file, getDataset(file, true));
            case "jpg", "jpeg" -> new JpegDecoder(file, getDataset(file, false));
            case "png" -> new PNGDecoder(file, getDataset(file, false));
            default -> throw new UnsupportedOperationException("Unsupported File Type: " + fileType);
        };
    }

    private static String detectFileTypeBySignature(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[8];
            int bytesRead = fis.read(header);

            if (bytesRead < 4) return null;

            if (startsWith(header, FILE_SIGNATURES.get("tif"))) return "tif";
            if (startsWith(header, FILE_SIGNATURES.get("tif_alt"))) return "tif";
            if (startsWith(header, FILE_SIGNATURES.get("ntf"))) return "ntf";
            if (startsWith(header, FILE_SIGNATURES.get("jpg"))) return "jpg";
            if (startsWith(header, FILE_SIGNATURES.get("png"))) return "png";

        } catch (IOException e) {
            System.err.println("Error reading file header: " + e.getMessage());
        }

        return null;
    }

    private static boolean startsWith(byte[] source, byte[] signature) {
        if (source.length < signature.length) return false;
        for (int i = 0; i < signature.length; i++) {
            if (source[i] != signature[i]) return false;
        }
        return true;
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
