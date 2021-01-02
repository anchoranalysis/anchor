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
package org.anchoranalysis.image.voxel.kernel.outline;

import java.util.function.IntSupplier;
import org.anchoranalysis.image.voxel.kernel.BinaryKernelTestBase;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.OutsideKernelPolicy;
import org.anchoranalysis.image.voxel.object.ObjectMaskFixture;

abstract class OutlineTestBase extends BinaryKernelTestBase {
    
    @Override
    protected int expectedInside2D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return fixture.expectedSurfaceNumberVoxels(useZFor2D(params));
    }
    
    @Override
    protected int expectedInside3D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return fixture.expectedSurfaceNumberVoxels(params.isUseZ());
    }
    
    @Override
    protected int expectedBoundary2D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return whenOutsideOff(params, fixture, () -> 8 );
    }
    
    @Override
    protected int expectedBoundary3D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return whenOutsideOff(params, fixture, () -> params.isUseZ() ? 36 : 24 );
    }
    
    private static int whenOutsideOff(KernelApplicationParameters params, ObjectMaskFixture fixture, IntSupplier otherwise) {
        if (params.getOutsideKernelPolicy()==OutsideKernelPolicy.AS_OFF) {
            return fixture.expectedSurfaceNumberVoxels(params.isUseZ());
        } else {
            return otherwise.getAsInt();
        }
    }
       
    private static boolean useZFor2D(KernelApplicationParameters params) {
        return params.isUseZ() && (!params.isIgnoreOutside() && !params.isOutsideHigh());
    }
}
