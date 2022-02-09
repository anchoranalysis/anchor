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
package org.anchoranalysis.image.bean.spatial.arrange.fill;

import java.util.LinkedList;
import java.util.List;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Creates {@link RGBStack}s that are incrementally larger.
 *
 * <p>The stacks grow larger exponentially.
 *
 * @author Owen Feehan
 */
class IncrementallyLargerStacksFixture {

    /** Size of first stack in the X dimension. */
    private static final int INITIAL_X = 10;

    /** Size of first stack in the Y dimension. */
    private static final int INITIAL_Y = 15;

    /** Multiplied by the previous X-size to form the new X-size at each increment. */
    private static final float GROWTH_RATIO_X = 1.5f;

    /** Multiplied by the previous Y-size to form the new Y-size at each increment. */
    private static final float GROWTH_RATIO_Y = 1.2f;

    /**
     * Create a number of stacks of different widths and heights, to match the number of colors in
     * {@code colorList}.
     *
     * <p>Initially they are very vertical (much longer height compared to width) but overtime they
     * become approximately similar, and then very horizontal (much longer width compared to
     * height).
     *
     * <p>Black will never be one of the employed colors. Nor will any individual color component be
     * zero-valued.
     *
     * @param colors the colors to create a stack for.
     * @return a newly created list of newly created {@link RGBStack} all of different sizes and
     *     colors, as per above.
     */
    public static List<RGBStack> createStacks(ColorList colors) {
        List<RGBStack> stacks = new LinkedList<>();
        float x = INITIAL_X;
        float y = INITIAL_Y;
        for (int i = 0; i < colors.size(); i++) {
            stacks.add(new RGBStack(new Extent((int) x, (int) y, 1), colors.get(i)));
            x *= GROWTH_RATIO_X;
            y *= GROWTH_RATIO_Y;
        }
        return stacks;
    }
}
