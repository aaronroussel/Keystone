package org.example.keystone.api;

import javafx.scene.image.Image;

import java.io.File;

public class ImageFactory {

    private static long maxCacheCapacityInMB = 800;
    // 800 MB max memory cache, maybe can be adjusted by the user later
    private static MemoryBoundImageCache cache = new MemoryBoundImageCache(maxCacheCapacityInMB);

    public static Image getFXImage(File file) {
        String fileExtension = MetadataDecoderFactory.getDriverName(file);
        System.out.println(maxCacheCapacityInMB);

        if (fileExtension == null) {
            throw new IllegalArgumentException("File extension error: null");
        }

        return switch (fileExtension) {
            case "JPEG", "PNG", "GTiff", "NITF" -> {
                System.out.println(ImageProcessor.isLargeImage(file));
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
            default -> throw new IllegalStateException("Unsupported file type: " + fileExtension);
        };
    }

    public static long getMaxCacheCapacityInMB() {
        System.out.println("[GET] maxCacheCapacityInMB = " + maxCacheCapacityInMB +
                " | ClassLoader = " + ImageFactory.class.getClassLoader() +
                " | Class = " + ImageFactory.class.hashCode());
        return cache.getMaxBytes();
    }

    public static void setMaxCacheCapacityInMB(long size) {
        ImageFactory.maxCacheCapacityInMB = size;
        cache.setMaxBytes(size);
        System.out.println("[SET] maxCacheCapacityInMB = " + maxCacheCapacityInMB +
                " | ClassLoader = " + ImageFactory.class.getClassLoader() +
                " | Class = " + ImageFactory.class.hashCode());
    }

}

