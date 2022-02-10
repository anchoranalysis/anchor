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
package org.anchoranalysis.image.bean.nonbean.spatial.align;

/**
 * How the alignment will occur for a particular dimension.
 *
 * @author Owen Feehan
 */
public enum AlignmentOnDimension {
    /** Aligns to the <b>minimum</b> position along the axis. */
    MIN {

        @Override
        public int align(int larger, int smaller, int disconsiderLeft) {
            return disconsiderLeft;
        }
    },

    /** Aligns to the <b>center</b> position along the axis. */
    CENTER {

        @Override
        public int align(int larger, int smaller, int disconsiderLeft) {
            int centered = (larger - disconsiderLeft - smaller) / 2;
            return disconsiderLeft + centered;
        }
    },

    /** Aligns to the <b>maximum</b> position along the axis. */
    MAX {

        @Override
        public int align(int larger, int smaller, int disconsiderLeft) {
            return Math.max(disconsiderLeft, larger - smaller);
        }
    };

    /**
     * Determine the minimum value after alignment.
     *
     * @param larger the larger space which is aligned with, disconsidering the first {@code
     *     disconsiderLeft} of it.
     * @param smaller the smaller space to align.
     * @param disconsiderLeft how much of {@code larger} to disconsider, left-most.
     * @return the minimum value of the smaller space after alignment.
     */
    public abstract int align(int larger, int smaller, int disconsiderLeft);
}
