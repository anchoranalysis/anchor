package org.anchoranalysis.image.bean.spatial.arrange.align;

import org.anchoranalysis.bean.AnchorBean;
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
 * @author Owen Feehan
 */
public abstract class BoxAligner extends AnchorBean<BoxAligner> {

    /**
     * Determines a {@link BoundingBox} to use for {@code smaller} so that it fits inside {@code l}.
     *
     * @param smaller the <b>smaller</b> bounding-box, relative to the minimum-corner of {@code
     *     larger}. Often this is {@code (0, 0, 0)} if it sits at the minimum corner of {@code
     *     larger}.
     * @param larger the <b>larger</b> bounding-box absolute coordinates.
     * @return absolute coordinates for {@code smaller}, while remaining inside {@code larger}.
     */
    public abstract BoundingBox align(BoundingBox smaller, BoundingBox larger);
}
