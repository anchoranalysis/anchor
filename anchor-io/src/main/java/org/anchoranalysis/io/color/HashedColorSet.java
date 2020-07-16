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

package org.anchoranalysis.io.color;

import java.util.HashMap;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.bean.color.generator.ColorSetGenerator;

// Associates an id with a color from an existing color set, and remembers the association
//  for next time
public class HashedColorSet implements ColorIndex {

    private HashMap<Integer, RGBColor> map = new HashMap<>();

    private ColorList colorList;

    private int crntIndex = 0;

    // Constructor
    public HashedColorSet(ColorSetGenerator colorSetGnrtr, int uniqueCols)
            throws OperationFailedException {
        super();
        this.colorList = colorSetGnrtr.generateColors(uniqueCols);
    }

    @Override
    public RGBColor get(int i) {
        Integer z = Integer.valueOf(i);
        RGBColor col = map.get(z);

        if (col == null) {
            col = colorList.get(crntIndex);

            // We reset if we have reached the end
            if (++crntIndex == colorList.size()) {
                crntIndex = 0;
            }

            // let's get the color
            map.put(z, col);
        }

        return col;
    }

    @Override
    public int numUniqueColors() {
        return colorList.numUniqueColors();
    }

    @Override
    public boolean has(int i) {
        return map.containsKey(i);
    }
}
