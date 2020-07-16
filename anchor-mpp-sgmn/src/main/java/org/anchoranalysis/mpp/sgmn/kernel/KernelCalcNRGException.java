/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.kernel;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

public class KernelCalcNRGException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 8395015058238556485L;

    public KernelCalcNRGException(String message) {
        super(message);
    }

    public KernelCalcNRGException(String message, Exception exc) {
        super(message, exc);
    }
}
