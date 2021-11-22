package org.anchoranalysis.spatial.orientation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cern.colt.matrix.DoubleMatrix2D;
import lombok.AllArgsConstructor;

/**
 * Shortcut means of asserting a particular value in the matrix.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class MatrixAsserter {

    private static final double DELTA_ROTATION_MATRIX = 1e-3;

    private final DoubleMatrix2D matrix;

    public void assertValue(double expectedValue, int indexRow, int indexColumn) {
        assertEquals(expectedValue, matrix.get(indexRow, indexColumn), DELTA_ROTATION_MATRIX);
    }
}
