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
package org.anchoranalysis.image.voxel.arithmetic;

import org.anchoranalysis.image.voxel.object.ObjectMask;

public interface VoxelsArithmetic {

    /**
     * Adds a constant-value to each voxel but <i>only</i> for voxels inside an object-mask
     *
     * @param object object-mask to restrict operation to certain voxels
     * @param valueToBeAdded constant-value to be added
     */
    void addTo(ObjectMask object, int valueToBeAdded);

    /**
     * Multiplies the value of all voxels by a factor.
     *
     * @param factor what to multiply-by
     */
    void multiplyBy(double factor);

    /**
     * Multiplies each voxel by constant factor but <i>only</i> for voxels inside an object-mask
     *
     * @param object object-mask to restrict operation to certain voxels
     * @param factor constant-value to multiply by
     */
    void multiplyBy(ObjectMask object, double factor);

    /**
     * Divides the value of all voxels by a scalar constant.
     *
     * @param divisor what to divide-by
     */
    void divideBy(int divisor);
    
    /**
     * Subtracts all current voxel-values from a constant-value
     *
     * <p>i.e. each voxel value {@code v} is updated to become {@code valueToSubtractFrom -v }
     *
     * @param valueToSubtractFrom the value to subtract from
     */
    void subtractFrom(int valueToSubtractFrom);
}
