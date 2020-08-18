package org.anchoranalysis.core.geometry.consumer;

/**
 * Like a {@link IntConsumer} but accepts three coordinates of a point
 * 
 * @author Owen Feehan
 *
 */
@FunctionalInterface
public interface PointThreeDimensionalConsumer {
    void accept(int x, int y, int z);
}