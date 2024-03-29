/*-
 * #%L
 * anchor-image-core
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.core.object.scale.method;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.object.scale.AccessObjectMaskSimple;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * Helps execute a test that scales objects.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TestHelper {

    /**
     * How much tolerance exists for the difference in scale between the scaled and unscaled
     * objects.
     */
    private static final double TOLERANCE_SCALE_DIFFERENCE = 0.5;

    private static final ScaleFactor SCALE_FACTOR = new ScaleFactor(4.2);

    /** The ratio in number of voxels expected after scaling. */
    private static final double EXPECTED_RATIO_INCREASE_VOLUME =
            SCALE_FACTOR.x() * SCALE_FACTOR.y();

    /**
     * Scales {@code objectsToScale} using the particular {@code method}, and checks the objects
     * individually have expected sizes.
     *
     * @param method the method to use to scale.
     * @param objectsToScale the objects to scale.
     * @return a map from the unscaled objects to the scaled objects.
     * @throws OperationFailedException if thrown by {@link ObjectScalingMethod#scaleElements(List,
     *     ScaleFactor, org.anchoranalysis.image.core.object.scale.AccessObjectMask)}.
     */
    public static Map<ObjectMask, ObjectMask> scaleAndTestIndividual(
            ObjectScalingMethod method, List<ObjectMask> objectsToScale)
            throws OperationFailedException {
        Map<ObjectMask, ObjectMask> scaled =
                method.scaleElements(objectsToScale, SCALE_FACTOR, new AccessObjectMaskSimple());

        for (ObjectMask object : objectsToScale) {
            TestHelper.checkIndividualObject(object, scaled);
        }

        return scaled;
    }

    /**
     * Checks that a scaled object has approximately the expected number of voxels, compared to the
     * unscaled object.
     *
     * @param object the object to check.
     * @param scaled a map from the unscaled objects to the scaled objects.
     */
    public static void checkIndividualObject(
            ObjectMask object, Map<ObjectMask, ObjectMask> scaled) {
        int numberVoxelsUnscaled = object.numberVoxelsOn();
        int numberVoxelsScaled = scaled.get(object).numberVoxelsOn();
        checkVolumeRatio(numberVoxelsUnscaled, numberVoxelsScaled);
    }

    /**
     * Checks that the expected-ratio exists between the number of voxels in the unscaled object,
     * and the scaled object.
     *
     * @param volumeUnscaled the number of voxels in the unscaled object.
     * @param volumeScaled the number of voxels in the scaled object.
     */
    public static void checkVolumeRatio(int volumeUnscaled, int volumeScaled) {
        double volumeRatio = ((double) volumeScaled) / volumeUnscaled;
        assertEquals(EXPECTED_RATIO_INCREASE_VOLUME, volumeRatio, TOLERANCE_SCALE_DIFFERENCE);
    }
}
