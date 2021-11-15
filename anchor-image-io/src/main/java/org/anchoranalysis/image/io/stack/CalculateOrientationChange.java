package org.anchoranalysis.image.io.stack;

import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.core.dimensions.OrientationChange;
import org.anchoranalysis.image.io.ImageIOException;

/**
 * Calculates any needed orientation change.
 *
 * @author Owen Feehan
 */
@FunctionalInterface
public interface CalculateOrientationChange {

    /**
     * Calculates any needed orientation change.
     *
     * @param logger the logger where any non-fatal errors can be written to. Fatal errors should be
     *     thrown as eceptions.
     * @return the orientation change needed.
     * @throws ImageIOException if it cannot be calculated.
     */
    OrientationChange calculateOrientationChange(Logger logger) throws ImageIOException;
}
