/* (C)2020 */
package org.anchoranalysis.annotation.io.assignment.generator;

import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.bean.color.generator.ColorSetGenerator;

public class ColorPool {

    private int numPaired;
    private ColorSetGenerator colorSetGeneratorPaired;
    private ColorSetGenerator colorSetGeneratorUnpaired;
    private boolean differentColorsForMatches;

    public ColorPool(
            int numPaired,
            ColorSetGenerator colorSetGeneratorPaired,
            ColorSetGenerator colorSetGeneratorUnpaired,
            boolean differentColorsForMatches) {
        this.numPaired = numPaired;
        this.colorSetGeneratorPaired = colorSetGeneratorPaired;
        this.colorSetGeneratorUnpaired = colorSetGeneratorUnpaired;
        this.differentColorsForMatches = differentColorsForMatches;
    }

    public ColorList createColors(int numberOtherObjects) throws OperationFailedException {

        ColorList cols = new ColorList();

        if (differentColorsForMatches) {

            // Matched
            cols.addAllScaled(colorSetGeneratorPaired.generateColors(numPaired), 0.5);

            // Unmatched
            cols.addAll(colorSetGeneratorUnpaired.generateColors(numberOtherObjects));
        } else {
            // Treat all as unmatched
            cols.addAll(colorSetGeneratorUnpaired.generateColors(numPaired + numberOtherObjects));
        }

        return cols;
    }

    public boolean isDifferentColorsForMatches() {
        return differentColorsForMatches;
    }
}
