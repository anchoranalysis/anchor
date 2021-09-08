package org.anchoranalysis.mpp.mark.conic;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CheckedUnsupportedOperationException;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * Utilities to help scale {@link Mark}s.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ScaleChecker {

    /**
     * Throws an exception of {@code scaleFactor} does not have identical X and Y components.
     *
     * @param scaleFactor the scaleFactor to check
     * @throws CheckedUnsupportedOperationException if the X and Y values of {@code scaleFactor}
     *     differ.
     */
    public static void checkIdenticalXY(ScaleFactor scaleFactor)
            throws CheckedUnsupportedOperationException {
        if (!scaleFactor.hasIdenticalXY()) {
            throw new CheckedUnsupportedOperationException(
                    "This operation is only supported if the scaleFactor is identical in X and Y dimensions.");
        }
    }
}
