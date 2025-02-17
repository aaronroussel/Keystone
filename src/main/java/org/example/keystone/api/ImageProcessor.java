package org.example.keystone.api;

import org.pf4j.ExtensionPoint;

import java.io.File;

public interface ImageProcessor extends ExtensionPoint {

    /*
        This is an interface that will serve as an extension point for plugins that assist in Image Processing.

        The ExtensionPoint class extended by this interface is a class provided by PF4J that allows plugins to
        implement this interface using the @Extension annotation.
     */

    String getMetadata(File file);
}



/*
        try {
            File tiffFile = new File("src/images/sample.tif");

            GeoTiffReader reader;
            reader = new GeoTiffReader(tiffFile);

            GeoTiffIIOMetadataDecoder metadata = reader.getMetadata();
            IIOMetadataNode mdNode = metadata.getRootNode();
            IIOMetadataDumper mdDump = new IIOMetadataDumper(mdNode);

            System.out.println(mdDump.getMetadata());
        } catch (Exception e) {
            e.printStackTrace();
        }
 */
