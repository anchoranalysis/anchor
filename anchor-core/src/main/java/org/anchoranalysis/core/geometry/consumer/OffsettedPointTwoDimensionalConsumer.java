package org.anchoranalysis.core.geometry.consumer;

/**
 * Like a {@link IntConsumer} but accepts two coordinates of a point and an offset value
 * 
 * @author Owen Feehan
 * @param <E> a checked-exception that can be thrown during consumption
 */
@FunctionalInterface
public interface OffsettedPointTwoDimensionalConsumer<E extends Exception> {
    
    /**
     * Accepts a point
     * 
     * @param x local x coordinate during iteration
     * @param y local y coordinate during iteration
     * @param offset offset based on current x and y values
     * @throws E
     */
    void accept(int x, int y, int offset) throws E;
}