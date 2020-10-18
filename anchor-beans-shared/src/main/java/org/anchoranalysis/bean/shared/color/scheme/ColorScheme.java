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

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.error.OperationFailedException;

/**
 * Creates a set of related colors.
 *
 * @author Owen Feehan
 */
public abstract class ColorScheme extends AnchorBean<ColorScheme> {

    /**
     * Creates a list of colors of particular size.
     *
     * @param size the size of the list
     * @return a newly created list with colors
     * @throws OperationFailedException
     */
    public abstract ColorList createList(int size) throws OperationFailedException;

    /**
     * Assigns a color to each index value from a list of size {@code numberColors}.
     *
     * <p>This allows non-contigous indices to each use a unique-ish color.
     *
     * <p>Indices will be assigned a unique color until the list has no more unique colors, at which
     * point they will be reused.
     *
     * <p><b>Beware</b> that as each index is remembered in a hash-map, this can become inefficient
     * for a large number of indices.
     *
     * @param numberColors the size of the list from which colors are selected
     * @return a newly created index
     * @throws OperationFailedException
     */
    public ColorIndex colorForEachIndex(int numberColors) throws OperationFailedException {
        return new AssignColorToEachIndex(createList(numberColors));
    }
}
