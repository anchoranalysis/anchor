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
package org.anchoranalysis.image.bean.spatial.arrange;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.spatial.box.Extent;

/** Two {@link RGBStack} of different sizes, with specific respective colors. */
public class DualStacks {

    /** The size of the <i>big</i> stack. */
    public static final Extent SIZE_BIG = new Extent(60, 50, 30);

    /**
     * The size of the <i>small</i> stack. This covers more than half of {@code SIZE_BIG} in the X
     * and Y dimensions but not in the Z.
     */
    public static final Extent SIZE_SMALL = new Extent(35, 30, 30);

    /**
     * Create a list containing the two {@link RGBStack}s.
     *
     * @param colorBig the color of all voxels in the <i>big</i> stack.
     * @param colorSmall the color of all voxels in the <i>small</i> stack.
     * @param flattenSmall if true, the smaller stack is flattened in the z-dimension to be a single
     *     z-slice instead of multiple slices.
     * @return a newly created list, containing the two respective (newly created) big and small
     *     {@link RGBStack}s.
     */
    public static List<RGBStack> asList(
            RGBColor colorBig, RGBColor colorSmall, boolean flattenSmall) {

        RGBStack big = new RGBStack(SIZE_BIG, colorBig);
        RGBStack small = new RGBStack(sizeSmall(flattenSmall), colorSmall);

        return Arrays.asList(big, small);
    }

    /** The size of the small image, depending on whether the z-dimension is flattened or not. */
    private static Extent sizeSmall(boolean flattenZ) {
        if (flattenZ) {
            return SIZE_SMALL.flattenZ();
        } else {
            return SIZE_SMALL;
        }
    }
}
