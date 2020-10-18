package org.anchoranalysis.spatial.point;

import org.anchoranalysis.core.arithmetic.RunningSum;
import org.anchoranalysis.core.arithmetic.RunningSumCollection;

/**
 * A running sum for tracking points in each dimension
 *
 * @author Owen Feehan
 */
public final class RunningSumPoint {

    private final RunningSumCollection delegate;

    public RunningSumPoint() {
        delegate = new RunningSumCollection(3);
    }

    public void increment(Point2i point) {
        forDim(0).increment(point.x());
        forDim(1).increment(point.y());
    }

    public void increment(Point2d point) {
        forDim(0).increment(point.x());
        forDim(1).increment(point.y());
    }

    public void increment(Point3i point) {
        forDim(0).increment(point.x());
        forDim(1).increment(point.y());
        forDim(2).increment(point.z());
    }

    public void increment(Point3d point) {
        forDim(0).increment(point.x());
        forDim(1).increment(point.y());
        forDim(2).increment(point.z());
    }

    public Point3d mean() {
        return new Point3d(forDim(0).mean(), forDim(1).mean(), forDim(2).mean());
    }

    public Point2d meanXY() {
        return new Point2d(forDim(0).mean(), forDim(1).mean());
    }

    /** The count for XY dimensions (guaranteed to always be the same */
    public long getCountXY() {
        return forDim(0).getCount();
    }

    /**
     * The count for Z dimension (this is identical to {@link #getCountXY} if only 3D points have
     * ben added)
     */
    public long getCountZ() {
        return forDim(2).getCount();
    }

    private RunningSum forDim(int dimIndex) {
        return delegate.get(dimIndex);
    }
}
