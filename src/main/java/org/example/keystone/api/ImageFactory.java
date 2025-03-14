package org.example.keystone.api;

import javafx.scene.image.Image;

import java.io.File;

public class ImageFactory {

    public static Image getFXImage(File file) {

        /*
          This is just a factory class used to create a javafx Image object from a file. This handles all file types we are supporting
          and consolidates the different logic into a single factory class. This will help reduce conditional logic on the front end.
          Please use this factory class for loading ALL types of images.
        */

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
