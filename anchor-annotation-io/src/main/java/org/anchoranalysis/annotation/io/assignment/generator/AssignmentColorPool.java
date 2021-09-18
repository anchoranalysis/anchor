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
import org.anchoranalysis.bean.shared.color.scheme.VeryBright;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.exception.OperationFailedException;

/**
 * Different {@link ColorScheme}s that can be used to select colors for paired and unpaired objects.
 * 
 * <p>This is useful when showing an assignment visually with the two objects side-by-side e.g. left and right.
 * 
 * @author Owen Feehan
 */
@AllArgsConstructor
public class AssignmentColorPool {

    /** The number of paired objects that exist in the assignment. */
    private final int numberPaired;

    /** The color-scheme used to generate colors for <i>paired</i> objects. */
    private final ColorScheme colorSchemePaired;
    
    /** The color-scheme used to generate colors for <i>unpaired</i> objects. */
    private final ColorScheme colorSchemeUnpaired;
    
    /** If true, different colors are used for paired objects, otherwise always the same color is used. */
    @Getter private final boolean differentColorsForPairs;
    
    /**
     * Creates with a number and colors for the paired objects, and defaults colors for unpaired.
     * 
     * @param numberPaired the number of paired objects that exist in the assignment.
     * @param colorSchemePaired the color-scheme used to generate colors for <i>paired</i> objects.
     */
    public AssignmentColorPool(int numberPaired, ColorScheme colorSchemePaired) {
        this(numberPaired, colorSchemePaired, new VeryBright(), true);
    }

    /**
     * Creates a list of colors to describe the assignment.
     * 
     * <p>The first {@code numberPaired} elements in this list, are colors to describe the paired elements.
     * 
     * <p>The remaining elements, are colors to describe the unpaired elements.
     * 
     * @param numberUnpaired the number of unapred objects that exist in this particular set of objects.
     * 
     * @return a list of elements of size {@code numberPaired + numberUnpaired} as described above.
     * @throws OperationFailedException if colors cannot be generated from the respective {@link ColorScheme}.
     */
    public ColorList createColors(int numberUnpaired) throws OperationFailedException {

        ColorList colors = new ColorList();

        if (differentColorsForPairs) {

            // Matched
            addAllScaled(colors, colorSchemePaired.createList(numberPaired), 0.5);

            // Unmatched
            colors.addAll(colorSchemeUnpaired.createList(numberUnpaired));
        } else {
            // Treat all as unpaired
            colors.addAll(colorSchemeUnpaired.createList(numberPaired + numberUnpaired));
        }

        return colors;
    }

    /**
     * Adds scaled versions of colors to a destination list.
     *
     * @param destination the list to add colors to.
     * @param toAdd the colors to add.
     * @param scale a scaling-factor applied to the intensity values as the colors are added to
     *     {@code destination}.
     */
    private static void addAllScaled(ColorList destination, ColorList toAdd, double scale) {
        for (RGBColor color : toAdd) {
            RGBColor scaled =
                    new RGBColor(
                            (int) (color.getRed() * scale),
                            (int) (color.getGreen() * scale),
                            (int) (color.getBlue() * scale));
            destination.add(scaled);
        }
    }
}
