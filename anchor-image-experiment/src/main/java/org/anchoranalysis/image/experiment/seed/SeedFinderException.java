/* (C)2020 */
package org.anchoranalysis.image.experiment.seed;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class SeedFinderException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = -5014516097016484634L;

    public SeedFinderException(String string) {
        super(string);
    }

    public SeedFinderException(Throwable exc) {
        super(exc);
    }
}
