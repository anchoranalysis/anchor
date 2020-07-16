/* (C)2020 */
package org.anchoranalysis.io.bioformats;

import loci.common.DebugTools;
import loci.common.LogbackTools;

/**
 * SINGLETON class for making sure Log4j is configured from a file log4j.xml that restricts the
 * output from the bioformats plugin
 *
 * @author Owen Feehan
 */
public class ConfigureBioformatsLogging {

    private static ConfigureBioformatsLogging instance;

    public static ConfigureBioformatsLogging instance() {
        if (instance == null) {
            instance = new ConfigureBioformatsLogging();
        }
        return instance;
    }

    private ConfigureBioformatsLogging() {
        // DISABLE ALL LOGGING FROM BIOFORMATS, can be "ERROR", "INFO" etc.
        //
        // This affects both the two systems Log4j and SL4J that bioformats might be using
        // See: LogbackTools.enableLogging("OFF'); and Log4jTools.enableLogging("OFF");		// NOSONAR
        //
        DebugTools.enableLogging("OFF");
        LogbackTools.setRootLevel("OFF");
    }

    /** Makes sure logging is configured */
    public void makeSureConfigured() {
        // Doesn't need to do anything further, as it should already be handled by the constructor
        // We keep this method, simply to give a concrete method to be called
    }
}
