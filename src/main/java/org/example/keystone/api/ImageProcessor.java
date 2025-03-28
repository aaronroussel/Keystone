package org.example.keystone.api;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import javafx.scene.image.Image;
import javafx.embed.swing.SwingFXUtils;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;

public class ImageProcessor {

    public static Image getBufferedImageGeoTiff(File file) {
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

    public static Image getBufferedImageNitf(File file) {
        gdal.AllRegister();
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
}
