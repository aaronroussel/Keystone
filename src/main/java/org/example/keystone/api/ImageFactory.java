package org.example.keystone.api;

import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;

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
            case "jpg", "png" -> new Image(file.toURI().toString());
            case "tif", "TIF" -> ImageProcessor.getBufferedImageGeoTiff(file);
            case "ntf" -> ImageProcessor.getBufferedImageNitf(file);
            default -> throw new IllegalStateException("Unsupported file type: " + fileExtension);
        };
    }
}
