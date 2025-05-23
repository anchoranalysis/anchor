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

package org.anchoranalysis.image.voxel.kernel.outline;

import java.util.function.Supplier;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.KernelPointCursor;

/**
 * Sets voxels on the <i>outline</i> of an object to be <i>on</i> and otherwise <i>off</i>.
 *
 * <p>In other words, given a binary-mask, it retains any <i>on</i> pixel that touches an <i>off</i>
 * pixel, and sets all other voxels to <i>off</i>.
 *
 * @author Owen Feehan
 */
public class OutlineKernel extends OutlineKernelBase {

    /**
     * Checks whether a particular neighbor voxel qualifies to make the current voxel an outline
     * voxel.
     */
    @Override
    protected boolean doesNeighborQualify(
            boolean inside,
            KernelPointCursor point,
            Supplier<UnsignedByteBuffer> buffer,
            int zShift) {
        if (inside) {
            return point.isBufferOff(buffer.get());
        } else {
            return point.isOutsideOffUnignored();
        }
    }
}
