package org.anchoranalysis.image.voxel.projection;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.ProjectableBuffer;
import org.anchoranalysis.image.voxel.datatype.FloatVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.spatial.box.Extent;
import org.junit.jupiter.api.Test;

/**
 * Base class for testing projections of multiple {@link Voxels} to create a single {@link Voxels}.
 *
 * @author Owen Feehan
 */
public abstract class ProjectionTestBase {

    private static final VoxelsAsserter ASSERTER =
            new VoxelsAsserter(ProjectableBufferFixture.EXTENT);

    @Test
    void testUnsignedByte() throws OperationFailedException {
        doTest(UnsignedByteVoxelType.INSTANCE);
    }

    @Test
    void testUnsignedShort() throws OperationFailedException {
        doTest(UnsignedShortVoxelType.INSTANCE);
    }

    @Test
    void testUnsignedInt() throws OperationFailedException {
        doTest(UnsignedIntVoxelType.INSTANCE);
    }

    @Test
    void testFloat() throws OperationFailedException {
        doTest(FloatVoxelType.INSTANCE);
    }

    /**
     * Creates the {@link ProjectableBuffer} to be tested.
     *
     * @param <T> the buffer-type that must match {@code voxelDataType}
     * @param voxelDataType the type of voxels to use in the buffer.
     * @param extent the size of the buffer.
     * @return a newly created {@link ProjectableBuffer} to be tested.
     * @throws OperationFailedException if thrown during projection.
     */
    protected abstract <T> ProjectableBuffer<T> createProjectableBuffer(
            VoxelDataType voxelDataType, Extent extent) throws OperationFailedException;

    /**
     * Assert that the results meet expectations.
     *
     * @param projection the final projection to check.
     * @param asserter convenience class to help with assertions.
     */
    protected abstract void assertTestResults(Voxels<?> projection, VoxelsAsserter asserter);

    private void doTest(VoxelDataType dataType) throws OperationFailedException {
        ProjectableBufferFixture<?> fixture = new ProjectableBufferFixture<>(dataType);
        Voxels<?> projection =
                fixture.calculate(
                        (voxelDataType, extent) ->
                                this.createProjectableBuffer(voxelDataType, extent));
        assertTestResults(projection, ASSERTER);
    }
}
