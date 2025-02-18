package org.example.keystone.api;

import org.pf4j.ExtensionPoint;

import java.util.Vector;

public interface MetadataProcessor extends ExtensionPoint {

    /*
        This is an interface that will serve as an extension point for plugins that assist in Image Processing.

        The ExtensionPoint class extended by this interface is a class provided by PF4J that allows plugins to
        implement this interface using the @Extension annotation.
     */
    Vector<String> getPrivateMetadata();

    Vector<String> getMetadataDomains();


}
