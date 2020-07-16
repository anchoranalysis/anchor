/* (C)2020 */
package org.anchoranalysis.io.output.bean;

import org.anchoranalysis.io.output.bean.allowed.AllOutputAllowed;
import org.anchoranalysis.io.output.bean.allowed.OutputAllowed;

public class OutputManagerPermissive extends OutputManagerWithPrefixer {

    @Override
    public boolean isOutputAllowed(String outputName) {
        return true;
    }

    @Override
    public OutputAllowed outputAllowedSecondLevel(String key) {
        return new AllOutputAllowed();
    }
}
