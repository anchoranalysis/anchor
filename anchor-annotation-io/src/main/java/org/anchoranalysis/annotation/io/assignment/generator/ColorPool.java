/*-
 * #%L
 * anchor-annotation-io
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

package org.anchoranalysis.annotation.io.assignment.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.bean.shared.color.scheme.ColorScheme;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.exception.OperationFailedException;

@AllArgsConstructor
public class ColorPool {

    private int numberPaired;
    private ColorScheme colorSchemePaired;
    private ColorScheme colorSchemeUnpaired;
    @Getter private boolean differentColorsForMatches;

    public ColorList createColors(int numberOtherObjects) throws OperationFailedException {

        ColorList colors = new ColorList();

        if (differentColorsForMatches) {

            // Matched
            colors.addAllScaled(colorSchemePaired.createList(numberPaired), 0.5);

            // Unmatched
            colors.addAll(colorSchemeUnpaired.createList(numberOtherObjects));
        } else {
            // Treat all as unmatched
            colors.addAll(colorSchemeUnpaired.createList(numberPaired + numberOtherObjects));
        }

        return colors;
    }
}
