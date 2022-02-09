/*-
 * #%L
 * anchor-image-voxel
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
