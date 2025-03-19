package org.example.keystone.api;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import javafx.scene.image.Image;
import javafx.embed.swing.SwingFXUtils;

public class ImageProcessor {

    public static Image getBufferedImage(File file) {
        // This Function serves the sole purpose for converting images not supported by JavaFX into a buffered image
        // which is then used to convert into a JavaFX Image object. This should be used specifically for GeoTIFF and
        // NITF images

        if (!file.exists() || !file.canRead()) {
            throw new IllegalArgumentException("Cannot read file at " + file.getPath());
        }

        RenderedOp renderedOp = JAI.create("fileload", file.getPath());
        BufferedImage bufferedImage = renderedOp.getAsBufferedImage();
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }
}
