package org.anchoranalysis.image.core.dimensions;

import java.util.Optional;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Changes orientation of an image in the XY plane.
 *
 * <p>The corrections refer to what rotation should be applied to the natural XY byte ordering in an
 * image file, to present in the orientation expected in the eventual image.
 *
 * <p>It is also possible for a mirroring to occur (X coordinates flipped in direction, but not in
 * the Y dimension) for each respective orientation change.
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

        @Override
        public Resolution resolution(Resolution resolution) {
            return resolution;
        }
    },

    /** No change in orientation, but mirroring across the X dimension. */
    MIRROR_WITHOUT_ROTATION {

        @Override
        public int index(int existingIndex, int x, int y, Extent extent) {
            return index(x, y, extent);
        }

        @Override
        public int index(int x, int y, Extent extent) {
            return extent.offset(extent.x() - x - 1, y);
        }

        @Override
        public Extent extent(Extent extent) {
            return extent;
        }

        @Override
        public Resolution resolution(Resolution resolution) {
            return resolution;
        }
    },

    /** Pixels should appear 90 degrees rotated in the anti-clockwise direction. */
    ROTATE_90_ANTICLOCKWISE {
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

        @Override
        public Resolution resolution(Resolution resolution) {
            return switchXY(resolution);
        }
    },

    /**
     * Pixels should appear 90 degrees rotated in the anticlockwise direction, <b>and then</b>
     * mirrored across the X-dimension.
     */
    ROTATE_90_ANTICLOCKWISE_MIRROR {
        @Override
        public int index(int existingIndex, int x, int y, Extent extent) {
            return index(x, y, extent);
        }

        @Override
        public int index(int x, int y, Extent extent) {
            return offsetSwitched(extent, y, x); // NOSONAR
        }

        @Override
        public Extent extent(Extent extent) {
            return switchXY(extent);
        }

        @Override
        public Resolution resolution(Resolution resolution) {
            return switchXY(resolution);
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

        @Override
        public Resolution resolution(Resolution resolution) {
            return resolution;
        }
    },

    /**
     * Pixels should appear 180 degrees rotated in the clockwise direction, <b>and then</b> mirrored
     * across the X-dimension.
     */
    ROTATE_180_MIRROR {
        @Override
        public int index(int existingIndex, int x, int y, Extent extent) {
            return index(x, y, extent);
        }

        @Override
        public int index(int x, int y, Extent extent) {
            return extent.offset(x, extent.y() - y - 1);
        }

        @Override
        public Extent extent(Extent extent) {
            return extent;
        }

        @Override
        public Resolution resolution(Resolution resolution) {
            return resolution;
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
            return offsetSwitched(extent, y, extent.x() - x - 1);
        }

        @Override
        public Extent extent(Extent extent) {
            return switchXY(extent);
        }

        @Override
        public Resolution resolution(Resolution resolution) {
            return switchXY(resolution);
        }
    },

    /**
     * Pixels should appear 90 degrees rotated clockwise, <b>and then</b> mirrored across the
     * X-dimension.
     */
    ROTATE_90_CLOCKWISE_MIRROR {
        @Override
        public int index(int existingIndex, int x, int y, Extent extent) {
            return index(x, y, extent);
        }

        @Override
        public int index(int x, int y, Extent extent) {
            return offsetSwitched(extent, extent.y() - y - 1, extent.x() - x - 1);
        }

        @Override
        public Extent extent(Extent extent) {
            return switchXY(extent);
        }

        @Override
        public Resolution resolution(Resolution resolution) {
            return switchXY(resolution);
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
     * A {@link Extent} that describes {@code extent} after the orientation change.
     *
     * @param extent the unchanged size.
     * @return either a newly created {@link Extent} if it is changed, or else {@code extent} if no
     *     change is needed.
     */
    public abstract Extent extent(Extent extent);

    /**
     * A {@link Resolution} that describes {@code resolution} after the orientation change.
     *
     * @param resolution the unchanged resolution.
     * @return either a newly created {@link Resolution} if it is changed, or else {@code
     *     resolution} if no change is needed.
     */
    public abstract Resolution resolution(Resolution resolution);

    /**
     * A {@link Dimensions} that describes {code dimensions} after the orientation change.
     *
     * @param dimensions the unchanged dimensions.
     * @return either a newly created {@link Dimensions} if it is changed, or else {@code
     *     dimensions} if no change is needed.
     */
    public Dimensions dimensions(Dimensions dimensions) {
        if (this == OrientationChange.KEEP_UNCHANGED) {
            return dimensions;
        } else {
            Extent extent = extent(dimensions.extent());
            Optional<Resolution> resolution = dimensions.resolution().map(this::resolution);
            return new Dimensions(extent, resolution);
        }
    }

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

    /** Switches the X- and Y- dimensions in a {@link Resolution}. */
    private static final Resolution switchXY(Resolution resolution) {
        try {
            return new Resolution(resolution.y(), resolution.x(), resolution.z());
        } catch (CreateException e) {
            throw new AnchorImpossibleSituationException();
        }
    }
}
