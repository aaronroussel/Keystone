package org.example.keystone.api;

import javafx.scene.image.Image;

import java.io.File;

public class ImageFactory {
    // 800 MB max memory cache, maybe can be adjusted by the user later
    private static final MemoryBoundImageCache cache = new MemoryBoundImageCache(800 * 1024 * 1024);

    public static Image getFXImage(File file) {
        String fileExtension = MetadataDecoderFactory.getFileExtension(file);

        if (fileExtension == null) {
            throw new IllegalArgumentException("File extension error: null");
        }

        return switch (fileExtension) {
            case "jpg", "tif", "TIF", "ntf" -> {
                if (ImageProcessor.isLargeImage(file)) {

                    Image cached = cache.get(file);
                    if (cached != null) {
                        yield cached;
                    }
                    Image subsampled = ImageProcessor.getSubsampledBufferedImage(file);
                    cache.put(file, subsampled);
                    yield subsampled;
                } else {
                    yield ImageProcessor.getBufferedImage(file);
                }
            }
            case "png" -> {
                yield ImageProcessor.getVectorBufferedImage(file);
            }
            default -> throw new IllegalStateException("Unsupported file type: " + fileExtension);
        };
    }
}

