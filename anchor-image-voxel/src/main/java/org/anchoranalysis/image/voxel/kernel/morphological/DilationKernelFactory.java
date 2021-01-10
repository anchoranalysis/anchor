/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.kernel.ConditionalKernel;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.spatial.point.Point3i;

@AllArgsConstructor
public class DilationKernelFactory {

    /** How the kernel is applied. */
    private final KernelApplicationParameters parameters;

    private final boolean bigNeighborhood;

    public BinaryKernel createDilationKernel(
            Optional<Voxels<UnsignedByteBuffer>> background, int minIntensityValue) {

        BinaryKernel kernelDilation = new DilationKernel(bigNeighborhood);

        if (minIntensityValue > 0 && background.isPresent()) {
            return new ConditionalKernel(kernelDilation, 
                point -> intensityCondition(background.get(), point, minIntensityValue));
        } else {
            return kernelDilation;
        }
    }

    public KernelApplicationParameters createParameters() {
        return parameters;
    }
    
    private static boolean intensityCondition(Voxels<UnsignedByteBuffer> voxels, Point3i point, int minIntensityValue) {
        return voxels.extract().voxel(point) >= minIntensityValue;
    }
}
