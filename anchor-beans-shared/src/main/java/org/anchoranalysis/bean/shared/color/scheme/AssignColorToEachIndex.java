/*-
 * #%L
 * anchor-io
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

package org.anchoranalysis.bean.shared.color.scheme;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;

/**
 * Associates an index with a color from an existing list, and remembers the association.
 *
 * <p>This allows non-contigous indices to each use a unique-ish color.
 *
 * <p>Indices will be assigned a unique color until the list has no more unique colors, at which
 * point they will be reused.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
class AssignColorToEachIndex implements ColorIndex {

    // START REQUIRED ARGUMENTS
    private final ColorList colorList;
    // END REQUIRED ARGUMENTS

    private Map<Integer, RGBColor> map = new HashMap<>();
    private int currentIndex = 0;

    @Override
    public RGBColor get(int index) {
        Integer indexBoxed = Integer.valueOf(index);
        RGBColor col = map.get(indexBoxed);

        if (col == null) {
            col = colorList.get(currentIndex);

            // We reset if we have reached the end
            if (++currentIndex == colorList.size()) {
                currentIndex = 0;
            }

            // let's get the color
            map.put(indexBoxed, col);
        }

        return col;
    }

    @Override
    public int numberUniqueColors() {
        return colorList.numberUniqueColors();
    }
}
