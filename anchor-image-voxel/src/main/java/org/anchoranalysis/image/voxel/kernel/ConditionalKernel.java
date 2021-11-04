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

import java.util.function.Predicate;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Executes another {@link BinaryKernel} iff a predicate is satisfied for a particular point.
 *
 * @author Owen Feehan
 */
public class ConditionalKernel extends BinaryKernel {

    private BinaryKernel kernel;
    private Predicate<Point3i> predicate;

    /**
     * Creates with a particular delegate {@link BinaryKernel} and a predicate that tests each
     * point.
     *
     * @param kernel the kernel to execute if {@code predicate} is satisfied at a particular point.
     * @param predicate the predicate to be applied to a point.
     */
    public ConditionalKernel(BinaryKernel kernel, Predicate<Point3i> predicate) {
        super(kernel.getSize());
        this.kernel = kernel;
        this.predicate = predicate;
    }

    @Override
    public boolean calculateAt(KernelPointCursor point) {

        if (predicate.test(point.getPoint())) {
            return kernel.calculateAt(point);
        } else {
            return false;
        }
    }

    @Override
    public void notifyBuffer(LocalSlices slices, int sliceIndex) {
        kernel.notifyBuffer(slices, sliceIndex);
    }
}
