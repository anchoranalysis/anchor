package org.anchoranalysis.image.voxel.projection.extrema;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.ProjectableBuffer;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.projection.ProjectableBufferFixture;
import org.anchoranalysis.image.voxel.projection.ProjectionTestBase;
import org.anchoranalysis.image.voxel.projection.VoxelsAsserter;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Tests {@link MaxIntensityProjection}.
 *
 * @author Owen Feehan
 */
class MaxIntensityProjectionTest extends ProjectionTestBase {

    @Override
    protected void assertTestResults(Voxels<?> projection, VoxelsAsserter asserter) {
        asserter.assertIncrementingSequence(
                ProjectableBufferFixture.INTIIAL_VALUE_THIRD_BUFFER, projection);
    }

    @Override
    protected <T> ProjectableBuffer<T> createProjectableBuffer(
            VoxelDataType voxelDataType, Extent extent) throws OperationFailedException {
        return MaxIntensityProjection.create(voxelDataType, extent);
    }
}
