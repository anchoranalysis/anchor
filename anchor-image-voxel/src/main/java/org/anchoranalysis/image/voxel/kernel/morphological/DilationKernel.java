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

import java.util.function.Supplier;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.KernelPointCursor;

/**
 * Morphological dilation with a 3x3 or 3x3x3 kernel size.
 *
 * @author Owen Feehan
 */
public final class DilationKernel extends BinaryKernelMorphological {

    /**
     * Creates with a type of neighborhood.
     * 
     * @param bigNeighborhood if true, a big neighborhood is used 2D-plane (8-connected instead of 4-connected), but not in
     * Z-direction (remains always 2-connected).
     */
    public DilationKernel(boolean bigNeighborhood) {
        super(bigNeighborhood, false, true);
    }

    @Override
    protected boolean firstCheck(KernelPointCursor point, UnsignedByteBuffer buffer) {
        return point.isBufferOff(buffer);
    }

    @Override
    protected boolean doesNeighborQualify(
            boolean inside,
            KernelPointCursor point,
            Supplier<UnsignedByteBuffer> buffer,
            int zShift) {
        if (inside) {
            return point.isBufferOn(buffer.get());
        } else {
            return point.isOutsideOn();
        }
    }
}
