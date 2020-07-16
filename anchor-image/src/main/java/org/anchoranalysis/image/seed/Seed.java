/* (C)2020 */
package org.anchoranalysis.image.seed;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * A seed provides an voxels that exclusively belong to a particular object, and can be used as a
 * starting hint for segmentation.
 *
 * @author Owen Feehan
 */
public interface Seed {

    ObjectMask createMask();

    void scaleXY(double scale) throws OperationFailedException;

    void flattenZ();

    void growToZ(int sz);

    Seed duplicate();

    boolean equalsDeep(Seed other);
}
