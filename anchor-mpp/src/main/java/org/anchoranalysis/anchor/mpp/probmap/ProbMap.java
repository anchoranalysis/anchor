/* (C)2020 */
package org.anchoranalysis.anchor.mpp.probmap;

import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.image.binary.mask.Mask;

public interface ProbMap extends PointSampler {

    Mask visualization() throws OptionalOperationUnsupportedException;
}
