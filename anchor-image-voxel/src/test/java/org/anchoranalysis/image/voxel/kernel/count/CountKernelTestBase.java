/*-
 * #%L
 * anchor-image-voxel
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
package org.anchoranalysis.image.voxel.kernel.count;

import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.KernelTestBase;
import org.anchoranalysis.image.voxel.kernel.apply.ApplyCountKernel;
import org.anchoranalysis.image.voxel.object.ObjectMaskFixture;

/**
 * Base class for tests that involve a {@link CountKernel}.
 *
 * @author Owen Feehan
 */
abstract class CountKernelTestBase extends KernelTestBase<CountKernel> {

    protected CountKernelTestBase() {
        super(new ApplyCountKernel());
    }

    @Override
    protected int expectedInside2D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return fixture.numberNeighbors(useZForExpectedInside(params));
    }

    @Override
    protected int expectedInside3D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return fixture.numberNeighbors(params.isUseZ());
    }

    @Override
    protected int expectedBoundary2D(
            ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return boundaryFromInside(expectedInside2D(fixture, params), params);
    }

    @Override
    protected int expectedBoundary3D(
            ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return boundaryFromInside(expectedInside3D(fixture, params), params);
    }

    /**
     * Whether to use the z-dimension when calculating {@link #expectedInside2D}.
     *
     * @param params the parameters
     * @return whether to useZ or not
     */
    protected abstract boolean useZForExpectedInside(KernelApplicationParameters params);

    /**
     * Calculates an expected number of voxels for an object adjacent to the boundary, given a value
     * for an object inside the scene.
     *
     * @param expectationForInside the expected number of voxels for an object fully inside the
     *     scene, not adjacent to a boundary.
     * @param params the parameters for applying the kernel.
     * @return the expected number of voxels for an object adjacent to the boundary.
     */
    protected abstract int boundaryFromInside(
            int expectationForInside, KernelApplicationParameters params);
}
