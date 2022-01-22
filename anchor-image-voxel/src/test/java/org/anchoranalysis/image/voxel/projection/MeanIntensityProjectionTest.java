package org.anchoranalysis.image.voxel.projection;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.ProjectableBuffer;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Tests {@link MeanIntensityProjection}.
 *
 * @author Owen Feehan
 */
class MeanIntensityProjectionTest extends ProjectionTestBase {

    @Override
    protected void assertTestResults(Voxels<?> projection, VoxelsAsserter asserter) {
        asserter.assertIncrementingSequence(
                ProjectableBufferFixture.INTIIAL_VALUE_SECOND_BUFFER, projection);
    }

    @Override
    protected <T> ProjectableBuffer<T> createProjectableBuffer(
            VoxelDataType voxelDataType, Extent extent) throws OperationFailedException {
        return MeanIntensityProjection.create(voxelDataType, extent);
    }
}
