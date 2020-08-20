package org.anchoranalysis.image.voxel.iterator;

/**
 * A binary operation on two int-values that produces an int
 * 
 * @author Owen Feehan
 *
 */
@FunctionalInterface
public interface IntBinaryOperation {

    int apply(int value1, int value2);
}
