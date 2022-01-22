package org.anchoranalysis.image.voxel.projection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.functional.unchecked.UnaryIntOperator;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.math.arithmetic.Counter;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Asserts the values of <i>all</i> voxels in a {@link Voxels}.
 *
 * <p>Any values are always exposed as {@code int}s irrespective of the underlying type.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class VoxelsAsserter {

    /** The size of the voxels that will be asserted. */
    private final Extent extent;

    /**
     * Assert that all voxels have a constant identical value.
     *
     * @param expectedValue the expected value of all voxels.
     * @param voxels the voxels to test.
     */
    public void assertConstantValue(int expectedValue, Voxels<?> voxels) {
        int[] expectedArray = createExpectedArray(index -> expectedValue);
        assertExpectedArray(expectedArray, voxels);
    }

    /**
     * Assert that the projection forms an incrementing sequence.
     *
     * <p>It is expected to increment in steps of one.
     *
     * @param expectedStartingValue the expected first value in the incrementing sequence.
     * @param voxels the voxels to test.
     */
    public void assertIncrementingSequence(int expectedStartingValue, Voxels<?> voxels) {
        Counter counter = new Counter(expectedStartingValue);
        int[] expectedArray = createExpectedArray(index -> counter.incrementReturn());
        assertExpectedArray(expectedArray, voxels);
    }

    /**
     * Assert a particular array of values.
     *
     * @param expectedValues the expected value of each voxel in standard indexed order.
     * @param voxels the voxels to test.
     */
    private void assertExpectedArray(int[] expectedValues, Voxels<?> voxels) {
        int offset = 0;
        for (int z = 0; z < extent.z(); z++) {
            for (int y = 0; y < extent.y(); y++) {
                for (int x = 0; x < extent.x(); x++) {
                    String message =
                            String.format(
                                    "position x=%d, y=%d, z=%d with offset=%d", x, y, z, offset);
                    assertEquals(expectedValues[offset], voxels.extract().voxel(x, y, z), message);
                    offset++;
                }
            }
        }
    }

    /**
     * Creates an array of {@code int} results with the appropriate size.
     *
     * @param operator creates a number for a given index in the array.
     */
    private int[] createExpectedArray(UnaryIntOperator operator) {
        int numberValues = extent.calculateVolumeAsInt();
        int[] array = new int[numberValues];
        for (int i = 0; i < numberValues; i++) {
            array[i] = operator.apply(i);
        }
        return array;
    }
}
