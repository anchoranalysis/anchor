package org.anchoranalysis.core.geometry.consumer;

import java.util.function.IntConsumer;

/**
 * Like a {@link IntConsumer} but accepts two coordinates of a point
 * 
 * @author Owen Feehan
 *
 */
@FunctionalInterface
public interface PointTwoDimensionalConsumer {
    void accept(int x, int y);
}