package org.anchoranalysis.image.bean.spatial.arrange.align;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Aligns a smaller {@link BoundingBox} to fit inside a larger {@link BoundingBox}.
 *
 * <p>The smaller box may also be identically sized as the larger.
 *
 * <p>This is useful for implementing different methods for aligning / growing a smaller image to
 * occupy a larger space. But it is not specific to images, and can be applied to any
 * bounding-boxes.
 *
 * <p>Growth never occurs in the z-dimension, and the size in this dimension should be equal for
 * both {@code smaller} and {@code larger}.
 *
 * @author Owen Feehan
 */
public abstract class BoxAligner extends AnchorBean<BoxAligner> {

    /**
     * Determines a {@link BoundingBox} to use for {@code smaller} {@link BoundingBox} so that it
     * fits inside {@code larger} {@link BoundingBox}.
     *
     * <p>The position and size of {@code smaller} and {@code larger} must be identical in the
     * z-dimension.
     *
     * @param smaller the <b>smaller</b> bounding-box, relative to the minimum-corner of {@code
     *     larger}. Often this is {@code (0, 0, 0)} if it sits at the minimum corner of {@code
     *     larger}.
     * @param larger the <b>larger</b> bounding-box absolute coordinates.
     * @return absolute coordinates for {@code smaller}, while remaining inside {@code larger}.
     * @throws OperationFailedException if the z-dimensions are not equal, or if an unrecognized
     *     parameter or illegal state exists preventing successful alignment.
     */
    public BoundingBox align(BoundingBox smaller, BoundingBox larger)
            throws OperationFailedException {
        checkCorner(smaller.cornerMin(), larger.cornerMin());
        checkExtent(smaller.extent(), larger.extent());
        return alignAfterCheck(smaller, larger);
    }

    /**
     * Determines a {@link BoundingBox} to use for a {@code smaller} {@link Extent} so that it fits
     * inside {@code larger} {@link Extent}.
     *
     * <p>The position and size of {@code smaller} and {@code larger} must be identical in the
     * z-dimension.
     *
     * @param smaller the <b>smaller</b> bounding-box, relative to the minimum-corner of {@code
     *     larger}. Often this is {@code (0, 0, 0)} if it sits at the minimum corner of {@code
     *     larger}.
     * @param larger the <b>larger</b> bounding-box absolute coordinates.
     * @return absolute coordinates for {@code smaller}, while remaining inside {@code larger}.
     * @throws OperationFailedException if the z-dimensions are not equal, or if an unrecognized
     *     parameter or illegal state exists preventing successful alignment.
     */
    public BoundingBox align(Extent smaller, Extent larger) throws OperationFailedException {
        checkExtent(smaller, larger);
        return alignAfterCheck(smaller, larger);
    }

    /**
     * Determines a {@link BoundingBox} to use for a {@code smaller} {@link Extent} so that it fits
     * inside {@code larger} {@link BoundingBox}.
     *
     * <p>The position and size of {@code smaller} and {@code larger} must be identical in the
     * z-dimension.
     *
     * @param smaller the <b>smaller</b> bounding-box, relative to the minimum-corner of {@code
     *     larger}. Often this is {@code (0, 0, 0)} if it sits at the minimum corner of {@code
     *     larger}.
     * @param larger the <b>larger</b> bounding-box absolute coordinates.
     * @return absolute coordinates for {@code smaller}, while remaining inside {@code larger}.
     * @throws OperationFailedException if the z-dimensions are not equal, or if an unrecognized
     *     parameter or illegal state exists preventing successful alignment.
     */
    public BoundingBox align(Extent smaller, BoundingBox larger) throws OperationFailedException {
        checkExtent(smaller, larger.extent());
        return alignAfterCheck(smaller, larger);
    }

    /**
     * Determines a {@link BoundingBox} to use for {@code smaller} {@link BoundingBox} so that it
     * fits inside {@code larger} {@link BoundingBox}.
     *
     * @param smaller the <b>smaller</b> bounding-box, relative to the minimum-corner of {@code
     *     larger}. Often this is {@code (0, 0, 0)} if it sits at the minimum corner of {@code
     *     larger}.
     * @param larger the <b>larger</b> bounding-box absolute coordinates.
     * @return absolute coordinates for {@code smaller}, while remaining inside {@code larger}.
     * @throws OperationFailedException if an unrecognized parameter or illegal state exists
     *     preventing successful alignment.
     */
    public abstract BoundingBox alignAfterCheck(BoundingBox smaller, BoundingBox larger)
            throws OperationFailedException;

    /**
     * Determines a {@link BoundingBox} to use for {@code smaller} {@link Extent} so that it fits
     * inside {@code larger} {@link Extent}.
     *
     * @param smaller the <b>smaller</b> bounding-box, relative to the minimum-corner of {@code
     *     larger}. Often this is {@code (0, 0, 0)} if it sits at the minimum corner of {@code
     *     larger}.
     * @param larger the <b>larger</b> bounding-box absolute coordinates.
     * @return absolute coordinates for {@code smaller}, while remaining inside {@code larger}.
     * @throws OperationFailedException if an unrecognized parameter or illegal state exists
     *     preventing successful alignment.
     */
    public abstract BoundingBox alignAfterCheck(Extent smaller, Extent larger)
            throws OperationFailedException;

    /**
     * Determines a {@link BoundingBox} to use for {@code smaller} {@link Extent} so that it fits
     * inside {@code larger} {@link BoundingBox}.
     *
     * @param smaller the <b>smaller</b> bounding-box, relative to the minimum-corner of {@code
     *     larger}. Often this is {@code (0, 0, 0)} if it sits at the minimum corner of {@code
     *     larger}.
     * @param larger the <b>larger</b> bounding-box absolute coordinates.
     * @return absolute coordinates for {@code smaller}, while remaining inside {@code larger}.
     * @throws OperationFailedException if an unrecognized parameter or illegal state exists
     *     preventing successful alignment.
     */
    public abstract BoundingBox alignAfterCheck(Extent smaller, BoundingBox larger)
            throws OperationFailedException;

    private static void checkCorner(ReadableTuple3i smaller, ReadableTuple3i larger)
            throws OperationFailedException {
        if (smaller.z() != 0) {
            throw new OperationFailedException(
                    String.format(
                            "The smaller bounding-box does not have 0 position in the z-dimension, rather: %d",
                            smaller.z()));
        }
    }

    private static void checkExtent(Extent smaller, Extent larger) throws OperationFailedException {
        if (smaller.z() != larger.z()) {
            throw new OperationFailedException(
                    String.format(
                            "The smaller and larger bounding-boxes do not have identical sizes in the z-dimension, respectively: %d and %d",
                            smaller.z(), larger.z()));
        }
    }
}
