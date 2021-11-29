package org.anchoranalysis.image.core.object.scale.method;

import java.util.Map;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.junit.jupiter.api.Test;

/**
 * Tests {@code ScaleObjectsIndependently}.
 *
 * @author Owen Feehan
 */
class ScaleObjectsIndependentlyTest {

    @Test
    void scaleOverlapping() throws OperationFailedException {
        Map<ObjectMask, ObjectMask> scaled =
                TestHelper.scaleAndTestIndividual(
                        new ScaleObjectsIndependently(), ObjectFixture.objectsOverlap());
        checkIntersection(scaled);
    }

    private void checkIntersection(Map<ObjectMask, ObjectMask> scaled) {
        int intersectionUnscaled =
                ObjectFixture.OBJECT_LEFT.countIntersectingVoxels(
                        ObjectFixture.OBJECT_RIGHT_OVERLAP);
        int intersectionScaled =
                scaled.get(ObjectFixture.OBJECT_LEFT)
                        .countIntersectingVoxels(scaled.get(ObjectFixture.OBJECT_RIGHT_OVERLAP));
        TestHelper.checkVolumeRatio(intersectionUnscaled, intersectionScaled);
    }
}
