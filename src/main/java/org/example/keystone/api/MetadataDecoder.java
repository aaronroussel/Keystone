package org.example.keystone.api;

import org.gdal.gdal.Dataset;

import java.io.File;
import java.util.Vector;

public class MetadataDecoder {
    // parent class for metadata decoders. decoders for specific file types should inherit this class.
    // should help reduce amount of repeated code.


    /*
        This will probably need to be completed and implemented AFTER we have a working base implementation for
        reading meta for each file type we are supporting, then once that is completed we can refactor into something
        that is structured better. The gdal functions operate basically the same for NITF and GeoTIFF, and may do so
        for other supported file types as well. It would be better to have the shared logic defined in a parent class
        to reduce the amount of repeated code.

        This class should also probably implement some kind of interface with generalized function definitions for common
        operations. This will tie in with pf4j for the plugin support. We have reading and writing metadata as core functionality
        and plugins will implement the specifics. This parent class should also implement that interface once its written. Again,
        this will probably be done AFTER we have working implementations for all of our supported file types, that way we have a
        big picture look at everything and can refactor from there into something with better structure than isolated classes with
        a lot of repeated code
     */


    //                              ----------------------WORK IN PROGRESS--------------------------
    protected Dataset dataset;
    protected File file;

    public Vector<String> getPrivateMetadata() {
        return null;
    }

    public Vector<String> getMetadataDomainList() {
        return null;
    }
}
