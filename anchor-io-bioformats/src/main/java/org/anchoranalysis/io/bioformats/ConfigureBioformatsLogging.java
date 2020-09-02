/*-
 * #%L
 * anchor-io-bioformats
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.io.bioformats;

import loci.common.DebugTools;
import loci.common.LogbackTools;

/**
 * Singleton class for making sure Bioformats is configured configured.
 * 
 * <p>This configuration disables logging from bioformats. 
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
