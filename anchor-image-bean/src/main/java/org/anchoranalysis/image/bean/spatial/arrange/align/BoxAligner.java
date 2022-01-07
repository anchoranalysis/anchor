package org.anchoranalysis.image.bean.spatial.arrange.align;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.spatial.box.BoundingBox;

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
     * Determines a {@link BoundingBox} to use for {@code smaller} so that it fits inside {@code l}.
     *
     * <p>The position and size of {@code smaller} and {@code larger} must be identical in the
     * z-dimension.
     *
     * @param smaller the <b>smaller</b> bounding-box, relative to the minimum-corner of {@code
     *     larger}. Often this is {@code (0, 0, 0)} if it sits at the minimum corner of {@code
     *     larger}.
     * @param larger the <b>larger</b> bounding-box absolute coordinates.
     * @return absolute coordinates for {@code smaller}, while remaining inside {@code larger}.
     * @throws OperationFailedException if the z-dimensions are not equal.
     */
    public BoundingBox align(BoundingBox smaller, BoundingBox larger)
            throws OperationFailedException {
        if (smaller.extent().z() != larger.extent().z()) {
            throw new OperationFailedException(
                    String.format(
                            "The smaller and larger bounding-boxes do not have identical sizes in the z-dimension, respectively: %d and %d",
                            smaller.extent().z(), larger.extent().z()));
        }
        if (smaller.cornerMin().z() != 0) {
            throw new OperationFailedException(
                    String.format(
                            "The smaller bounding-box does not have 0 position in the z-dimension, rather: %d",
                            smaller.cornerMin().z()));
        }
        return alignAfterCheck(smaller, larger);
    }

    /**
     * Determines a {@link BoundingBox} to use for {@code smaller} so that it fits inside {@code l}.
     *
     * @param smaller the <b>smaller</b> bounding-box, relative to the minimum-corner of {@code
     *     larger}. Often this is {@code (0, 0, 0)} if it sits at the minimum corner of {@code
     *     larger}.
     * @param larger the <b>larger</b> bounding-box absolute coordinates.
     * @return absolute coordinates for {@code smaller}, while remaining inside {@code larger}.
     */
    public abstract BoundingBox alignAfterCheck(BoundingBox smaller, BoundingBox larger);
}
