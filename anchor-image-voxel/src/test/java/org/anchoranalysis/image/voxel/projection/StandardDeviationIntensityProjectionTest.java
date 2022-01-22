package org.anchoranalysis.image.voxel.projection;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.ProjectableBuffer;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Tests {@link StandardDeviationIntensityProjection}.
 *
 * @author Owen Feehan
 */
class StandardDeviationIntensityProjectionTest extends ProjectionTestBase {

    /**
     * The standard deviation should be the same for all voxels, as the spacing between the three
     * voxels is always identical.
     */
    private static final int EXPECTED_STANDARD_DEVIATION_AS_INT = 8;

    @Override
    protected void assertTestResults(Voxels<?> projection, VoxelsAsserter asserter) {
        asserter.assertConstantValue(EXPECTED_STANDARD_DEVIATION_AS_INT, projection);
    }

    @Override
    protected <T> ProjectableBuffer<T> createProjectableBuffer(
            VoxelDataType voxelDataType, Extent extent) throws OperationFailedException {
        return StandardDeviationIntensityProjection.create(voxelDataType, extent);
    }
}
