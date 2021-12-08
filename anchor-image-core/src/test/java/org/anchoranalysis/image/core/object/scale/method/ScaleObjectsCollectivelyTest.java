package org.anchoranalysis.image.core.object.scale.method;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.junit.jupiter.api.Test;

/**
 * Tests {@code ScaleObjectsCollectively}.
 *
 * @author Owen Feehan
 */
class ScaleObjectsCollectivelyTest {

    @Test
    void testScaleCollectively() throws OperationFailedException {

        Map<ObjectMask, ObjectMask> scaled =
                TestHelper.scaleAndTestIndividual(
                        new ScaleObjectsCollectively(), ObjectFixture.objectsAdjacent());

        checkIdenticalObjects(scaled);
    }

    /** Check that the two scaled objects have identical volumes after scaling. */
    private static void checkIdenticalObjects(Map<ObjectMask, ObjectMask> scaled) {
        assertEquals(
                scaled.get(ObjectFixture.OBJECT_LEFT).numberVoxelsOn(),
                scaled.get(ObjectFixture.OBJECT_RIGHT_ADJACENT).numberVoxelsOn());
    }
}
