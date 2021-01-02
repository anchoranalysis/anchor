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
package org.anchoranalysis.image.voxel.kernel.morphological;

import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.Extent;

/**
 * Tests {@link DilationKernelZOnly}.
 * 
 * @author Owen Feehan
 *
 */
class DilationKernelZOnlyTest extends MorphologicalKernelTestBase {
    
    private static final ExpectedValues VALUES_2D = new ExpectedValues(EXPECTED_MAXIMAL_2D, 20, 20, 20);
    
    @Override
    protected BinaryKernel createKernel(ObjectMask object, Extent extentScene) {
        return new DilationKernelZOnly();
    }
    
    @Override
    protected ExpectedValues inside2D() {
        return VALUES_2D;
    }

    @Override
    protected ExpectedValues inside3D() {
        return new ExpectedValues(280, 60, 100, 60);
    }
    
    @Override
    protected ExpectedValues boundary2D() {
        return VALUES_2D;
    }
    
    @Override
    protected ExpectedValues boundary3D() {
        return new ExpectedValues(240, 60, 80, 60);
    }
}
