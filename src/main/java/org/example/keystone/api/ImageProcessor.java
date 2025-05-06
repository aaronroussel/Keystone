package org.example.keystone.api;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.stream.ImageInputStream;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;

import javafx.scene.image.Image;
import javafx.embed.swing.SwingFXUtils;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdalconst.gdalconstConstants;

public class ImageProcessor {

   private static long maxSubsampledImageSizeInMB = 100L;

   public static Image getBufferedImage(File file) {

        Dataset dataset = gdal.Open(file.getAbsolutePath());
        if (dataset == null) {
            throw new RuntimeException("Failed to open file with GDAL: " + file.getAbsolutePath());
        }

        // we need to get the image dimensions and raster count
        int width = dataset.getRasterXSize();
        int height = dataset.getRasterYSize();
        int bands = dataset.getRasterCount();

        // make sure we actually have bands
        if (bands < 1) {
            throw new RuntimeException("Image has no raster bands.");
        }

        BufferedImage bufferedImage;

        if (bands >= 3) {
            // if we have 3 or more bands then our image is RGB (we are assuming)
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            //IMPORTANT: This writable raster object holds a reference to the buffered image object. When we set pixels using
            // this object, it directly modifies the pixel data in the buffered image. The BufferedImage class does not provide
            // methods to directly modify individual pixels directly. For this reason, we are using WritableRaster.
            WritableRaster raster = bufferedImage.getRaster();
            // initialize arrays for red, green, and blue scanlines. This represents a single row of pixel values
            int[] r = new int[width];
            int[] g = new int[width];
            int[] b = new int[width];

            // GDAL uses 1-based indexes for obtaining raster bands
            Band redBand = dataset.GetRasterBand(1);
            Band greenBand = dataset.GetRasterBand(2);
            Band blueBand = dataset.GetRasterBand(3);

            // read pixel values from all 3 bands for each row
            for (int y = 0; y < height; y++) {
                redBand.ReadRaster(0, y, width, 1, r);
                greenBand.ReadRaster(0, y, width, 1, g);
                blueBand.ReadRaster(0, y, width, 1, b);

                for (int x = 0; x < width; x++) {
                    // int array represents the pixel r,g,b value
                    int[] newPixel = {r[x], g[x], b[x]};
                    raster.setPixel(x, y, newPixel);
                }
            }
        } else {
            // if less than 3 bands we assume grayscale. Here we just read in one band and fill in each pixel row by row
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            WritableRaster raster = bufferedImage.getRaster();

            Band grayBand = dataset.GetRasterBand(1);
            int[] pixels = new int[width];

            for (int y = 0; y < height; y++) {
                grayBand.ReadRaster(0, y, width, 1, pixels);
                for (int x = 0; x < width; x++) {
                    raster.setSample(x, y, 0, pixels[x]);
                }
            }
        }

        return SwingFXUtils.toFXImage(bufferedImage, null);
   }

    public static Dataset convertToGeoTiff(File inputFile, File outputFile) throws Exception {
        return gdal.GetDriverByName("GTiff").CreateCopy(outputFile.getAbsolutePath(),gdal.Open(inputFile.getAbsolutePath()));
    }




    public static Image getSubsampledBufferedImage(File file) {
       System.out.println(maxSubsampledImageSizeInMB);
        int stepSize = getStepSize(file);
        Dataset dataset = gdal.Open(file.getAbsolutePath());
        if (dataset == null) {
            throw new RuntimeException("Failed to open file with GDAL: " + file.getAbsolutePath());
        }

        int width = dataset.getRasterXSize();
        int height = dataset.getRasterYSize();
        int bands = dataset.getRasterCount();

        System.out.println("Width = " + width + ", Height = " + height);

        if (bands < 1) {
            throw new RuntimeException("Image has no raster bands.");
        }

        int subsampledWidth = width / stepSize;
        int subsampledHeight = height / stepSize;

        BufferedImage bufferedImage;

        if (bands >= 3) {
            bufferedImage = new BufferedImage(subsampledWidth, subsampledHeight, BufferedImage.TYPE_INT_RGB);
            WritableRaster raster = bufferedImage.getRaster();

            Band redBand = dataset.GetRasterBand(1);
            Band greenBand = dataset.GetRasterBand(2);
            Band blueBand = dataset.GetRasterBand(3);

            int[] rFull = new int[width];
            int[] gFull = new int[width];
            int[] bFull = new int[width];

            for (int y = 0, j = 0; y < height && j < subsampledHeight; y += stepSize, j++) {
                redBand.ReadRaster(0, y, width, 1, rFull);
                greenBand.ReadRaster(0, y, width, 1, gFull);
                blueBand.ReadRaster(0, y, width, 1, bFull);

                for (int x = 0, i = 0; x < width && i < subsampledWidth; x += stepSize, i++) {
                    int[] pixel = {rFull[x], gFull[x], bFull[x]};
                    raster.setPixel(i, j, pixel);
                }
            }

            redBand.delete();
            greenBand.delete();
            blueBand.delete();

            redBand = null;
            greenBand = null;
            blueBand = null;
        } else {
            bufferedImage = new BufferedImage(subsampledWidth, subsampledHeight, BufferedImage.TYPE_BYTE_GRAY);
            WritableRaster raster = bufferedImage.getRaster();

            Band grayBand = dataset.GetRasterBand(1);
            int[] rowFull = new int[width];

            for (int y = 0, j = 0; y < height && j < subsampledHeight; y += stepSize, j++) {
                grayBand.ReadRaster(0, y, width, 1, rowFull);
                for (int x = 0, i = 0; x < width && i < subsampledWidth; x += stepSize, i++) {
                    raster.setSample(i, j, 0, rowFull[x]);
                }
            }

            grayBand.delete();

            grayBand = null;
        }

        dataset.delete();
        dataset = null;

        System.gc();

        return SwingFXUtils.toFXImage(bufferedImage, null);
    }


    public static Image getVectorBufferedImage(File file) {
        Dataset dataset = gdal.Open(file.getAbsolutePath());
        if (dataset == null) {
            System.err.println("Failed to load image: " + gdal.GetLastErrorMsg());
            return null;
        }

        int width = dataset.getRasterXSize();
        int height = dataset.getRasterYSize();
        int bands = dataset.getRasterCount();

        if (bands < 1 || bands > 4) {
            System.err.println("Unsupported number of bands: " + bands);
            dataset.delete();
            return null;
        }

        BufferedImage bufferedImage = new BufferedImage(
                width,
                height,
                bands == 4 ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR
        );

        byte[] imageData = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();

        int[] channelMap = (bands == 4) ? new int[]{3, 2, 1, 0} : new int[]{2, 1, 0};

        byte[] bandBuffer = new byte[width * height];

        for (int i = 0; i < bands; i++) {
            Band band = dataset.GetRasterBand(i + 1);
            band.ReadRaster(0, 0, width, height, bandBuffer);

            int channel = channelMap[i];
            for (int j = 0; j < width * height; j++) {
                imageData[j * bands + channel] = bandBuffer[j];
            }
        }

        dataset.delete(); // Cleanup
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }


    public static boolean isLargeImage(File file) {
        long bytes = file.length();
        long threshold = maxSubsampledImageSizeInMB * 1024L * 1024L;
        return bytes > threshold;
    }

    public static int getStepSize(File file) {
        long MAX_MEMORY_USAGE_BYTES = maxSubsampledImageSizeInMB * 1024 * 1024;
        int BYTES_PER_PIXEL = 3;

        long fileSizeBytes = file.length();

        long totalPixels = fileSizeBytes / BYTES_PER_PIXEL;

        long maxPixelsAllowed = MAX_MEMORY_USAGE_BYTES / BYTES_PER_PIXEL;

        double scaleFactor = Math.sqrt((double) totalPixels / maxPixelsAllowed);
        int stepSize = (int) Math.ceil(scaleFactor);

        System.out.println(Math.max(stepSize, 1));

        return Math.max(stepSize, 1);
    }

    public static long getMaxSubsampledImageSizeInMB() {
        return maxSubsampledImageSizeInMB;
    }

    public static void setMaxSubsampledImageSizeInMB(long maxSubsampledImageSizeInMB) {
        ImageProcessor.maxSubsampledImageSizeInMB = maxSubsampledImageSizeInMB;
    }
}
