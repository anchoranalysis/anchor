/*-
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.bean.spatial.arrange.tile;

import lombok.Getter;

/** All the sizes for a particular dimension, as well as the sum of the sizes. */
class SizesAcrossDimension {

    /** The sizes and corner points for the dimension. */
    private SizeAtPoint[] sizes;

    /** The sum of the sizes across the dimension. */
    @Getter private int sum;

    /** The current index, of the next element to be added. */
    private int index = 0;

    public SizesAcrossDimension(int numberElements) {
        sizes = new SizeAtPoint[numberElements];
    }

    /**
     * Adds a new element of a particular {@code size}.
     *
     * @param size the size of the element in the particular dimension.
     */
    public void add(int size) {
        sizes[index] = new SizeAtPoint(sum, size);
        sum += size;
        index++;
    }

    /**
     * Gets the size corresponding at a particular position.
     *
     * @param index the index of the element (zero-indexed).
     * @return the corresponding size.
     */
    public SizeAtPoint get(int index) {
        return sizes[index];
    }
}
