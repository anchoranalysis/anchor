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

/**
 * Combines two {@Binary Kernel}s so that they only a true value exists only at a point where both
 * kernels return true.
 *
 * <p>This implements a <i>logical and</i> operator.
 *
 * @author Owen Feehan
 */
public class AndKernel extends BinaryKernel {

    private final BinaryKernel kernel1;
    private final BinaryKernel kernel2;

    /**
     * Create for the two kernels.
     *
     * @param kernel1 the first kernel.
     * @param kernel2 the second kernel.
     */
    public AndKernel(BinaryKernel kernel1, BinaryKernel kernel2) {
        super(kernel1.getSize());
        this.kernel1 = kernel1;
        this.kernel2 = kernel2;
    }

    @Override
    public void notifyZChange(LocalSlices inSlices, int z) {
        kernel1.notifyZChange(inSlices, z);
        kernel2.notifyZChange(inSlices, z);
    }

    @Override
    public boolean calculateAt(KernelPointCursor point) {
        return kernel1.calculateAt(point) && kernel2.calculateAt(point);
    }
}
