package org.example.keystone.api;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;

import java.io.File;
import java.util.Vector;

public class MetadataDecoder {
    // parent class for metadata decoders. decoders for specific file types should inherit this class.
    // should help reduce amount of repeated code.
    // WORK IN PROGRESS
    protected final File file;
    protected Dataset dataset;
}
