package org.example.keystone.api;

import javafx.scene.image.Image;

import java.io.File;

public class ImageFactory {

    public static Image getFXImage(File file) {
        String fileExtension = MetadataDecoderFactory.getFileExtension(file);

        if (fileExtension == null) {
            throw new IllegalArgumentException("File extension error: null");
        }
        return switch (fileExtension) {
            case "jpeg", "png" -> new Image(file.getPath());
            case "ntf", "tif", "TIF" -> ImageProcessor.getBufferedImage(file);
            default -> throw new IllegalStateException("Unsupported file type: " + fileExtension);
        };
    }
}
