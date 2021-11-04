package org.anchoranalysis.image.voxel.extracter;

import org.anchoranalysis.spatial.box.Extent;

/**
 * Changes orientation of an image in the XY plane.
 *
 * <p>The corrections refer to what rotation should be applied to the natural XY byte ordering in an
 * image file, to present in the orientation expected in the eventual image.
 *
 * @author Owen Feehan
 */
public enum OrientationChange {

    /** No change in orientation. */
    KEEP_UNCHANGED {
        @Override
        public int index(int existingIndex, int x, int y, Extent extent) {
            return existingIndex;
        }

        @Override
        public int index(int x, int y, Extent extent) {
            return extent.offset(x, y);
        }

        @Override
        public Extent extent(Extent extent) {
            return extent;
        }
    },

    /**
     * Pixels should appear 90 degrees rotated in the clockwise direction.
     *
     * <p>This is equivalent to rotating 270 degrees anti-clockwise.
     */
    ROTATE_90_CLOCKWISE {
        @Override
        public int index(int existingIndex, int x, int y, Extent extent) {
            return index(x, y, extent);
        }

        @Override
        public int index(int x, int y, Extent extent) {
            return offsetSwitched(extent, extent.y() - y - 1, x);
        }

        @Override
        public Extent extent(Extent extent) {
            return switchXY(extent);
        }
    },

    /** Pixels should appear 180 degrees rotated in the clockwise direction. */
    ROTATE_180 {
        @Override
        public int index(int existingIndex, int x, int y, Extent extent) {
            return index(x, y, extent);
        }

        @Override
        public int index(int x, int y, Extent extent) {
            return extent.offset(extent.x() - x - 1, extent.y() - y - 1);
        }

        @Override
        public Extent extent(Extent extent) {
            return extent;
        }
    },

    /**
     * Pixels should appear 270 degrees rotated in the clockwise direction.
     *
     * <p>This is equivalent to rotating 90 degrees anti-clockwise.
     */
    ROTATE_270_CLOCKWISE {
        @Override
        public int index(int existingIndex, int x, int y, Extent extent) {
            return index(x, y, extent);
        }

        @Override
        public int index(int x, int y, Extent extent) {
            return offsetSwitched(extent, y, extent.x() - x - 1);
        }

        @Override
        public Extent extent(Extent extent) {
            return switchXY(extent);
        }
    };

    /**
     * Determines the index in a output-array to put an element after applying the
     * orientation-change.
     *
     * @param x intended output voxel position on the x-axis, before any orientation-change.
     * @param y intended output voxel position on the y-axis, before any orientation-change.
     * @param extent image-size, before any orientation-change.
     * @return the changed index.
     */
    public abstract int index(int x, int y, Extent extent);

    /**
     * Like {#link determineOutputIndex(int,int,Extent)} but also accepts an index.
     *
     * @param existingIndex an index that is already correct, if no orientation-change is applied.
     * @param x intended output voxel position on the x-axis, before any orientation-change.
     * @param y intended output voxel position on the y-axis, before any orientation-change.
     * @param extent image-size, before any orientation-change.
     * @return the changed index.
     */
    public abstract int index(int existingIndex, int x, int y, Extent extent);

    /**
     * Creates a new {@link Extent} that describes {@code extent} after the orientation change.
     *
     * @param extent the unchanged size.
     * @return the size after the change.
     */
    public abstract Extent extent(Extent extent);

    /**
     * Calculates a XY-offset of a point in an {@link Extent}, switching the X and Y dimensions in
     * the {@link Extent}.
     *
     * @param extent the {@link Extent} the points reside in, unswitched.
     * @param x the value in the X-dimension for the point, unswitched.
     * @param y the value in the Y-dimension for the point, unswitched.
     * @return the offset if the X and Y dimensions in the {@link Extent} had been switched.
     */
    private static final int offsetSwitched(Extent extent, int x, int y) {
        return (y * extent.y()) + x;
    }

    /** Switches the X- and Y- dimensions in a {@link Extent}. */
    private static final Extent switchXY(Extent extent) {
        return new Extent(extent.y(), extent.x(), extent.z());
    }
}
