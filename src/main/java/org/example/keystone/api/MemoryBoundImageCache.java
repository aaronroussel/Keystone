package org.example.keystone.api;

import java.io.File;
import java.util.*;

public class MemoryBoundImageCache {
    private final LinkedHashMap<File, javafx.scene.image.Image> cache;
    private final long maxBytes;
    private long currentBytes = 0;

    public MemoryBoundImageCache(long maxBytes) {
        this.maxBytes = maxBytes;

        this.cache = new LinkedHashMap<>(16, 0.75f, true);
    }

    public synchronized javafx.scene.image.Image get(File key) {
        return cache.get(key);
    }

    public synchronized void put(File key, javafx.scene.image.Image image) {
        long imageSize = estimateImageSizeBytes(image);

        // check if there is space in the cache for the new image, and evict other images to make space if needed
        while (!cache.isEmpty() && currentBytes + imageSize > maxBytes) {
            Map.Entry<File, javafx.scene.image.Image> oldest = cache.entrySet().iterator().next();
            currentBytes -= estimateImageSizeBytes(oldest.getValue());
            cache.remove(oldest.getKey());
        }

        cache.put(key, image);
        currentBytes += imageSize;
    }

    private long estimateImageSizeBytes(javafx.scene.image.Image image) {
        // image size estimation uses : width * height * 4 bytes per pixel
        return (long) image.getWidth() * (long) image.getHeight() * 4L;
    }
}
