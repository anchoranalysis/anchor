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
        return new StandardDeviationIntensityProjection().create(voxelDataType, extent);
    }
}
