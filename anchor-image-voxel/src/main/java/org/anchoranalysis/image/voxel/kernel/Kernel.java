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

package org.anchoranalysis.image.voxel.kernel;

import com.google.common.base.Preconditions;
import lombok.Getter;

/**
 * A small matrix that is convolved with or otherwise combined across each voxel in an image.
 *
 * <p>See <a href="https://en.wikipedia.org/wiki/Kernel_(image_processing)">Kernel (image
 * processing)</a>.
 *
 * <p>The matrix is assumed always to be 2D and <i>square</i> i.e. the same size in X and Y.
 *
 * @author Owen Feehan
 */
public abstract class Kernel {

    /**
     * The size of the kernel-matrix in one dimension, either X or Y.
     *
     * <p>This size should always be an <i>odd</i> number.
     */
    @Getter private int size;

    /**
     * Creates a kernel with a given size in the X and Y dimensions.
     *
     * @param size the number of pixels in a given dimension, which must be an <i>odd</i> number.
     */
    protected Kernel(int size) {
        Preconditions.checkArgument((size % 2) == 1);
        this.size = size;
    }

    /**
     * Called to inform the {@link Kernel} of buffers that are currently being processed.
     *
     * <p>Only a single z-slice is processed at a particular time.
     *
     * <p>This should be repeatedly called each time the focus changes to a different z-slice.
     *
     * @param slices slices that can be processed by the kernel, localized to a current <i>local</i>
     *     area of focus.
     * @param sliceIndex the global z-index of the slice that is currently the focus of the kernel.
     */
    public abstract void notifyBuffer(LocalSlices slices, int sliceIndex);
}
