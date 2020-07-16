/* (C)2020 */
package org.anchoranalysis.image.object;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Calculates overlap between object-masks
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OverlapCalculator {

    public static double calcOverlapRatio(ObjectMask objA, ObjectMask objB, ObjectMask objMerged) {

        int intersectingVoxels = objA.countIntersectingVoxels(objB);
        if (intersectingVoxels == 0) {
            return 0;
        }

        int vol = objMerged.numberVoxelsOn();

        return ((double) intersectingVoxels) / vol;
    }
}
