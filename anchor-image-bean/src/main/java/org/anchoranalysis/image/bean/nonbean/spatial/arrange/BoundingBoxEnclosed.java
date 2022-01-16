package org.anchoranalysis.image.bean.nonbean.spatial.arrange;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * Describes a {@link BoundingBox} that is optionally enclosed by a larger containing {@link
 * BoundingBox} to given padding.
 *
 * <p>No checks currently occur that {@code enclosing} fully contains {@code box}.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class BoundingBoxEnclosed {

    /** The {@link BoundingBox} in which the image should be placed. This excludes padding. */
    @Getter private BoundingBox box;

    /**
     * An enclosing box (equal to or larger than {@code box} covering all screen-space used for this
     * entity, including padding.
     *
     * <p>This should be identical to {@code box} if there is no padding.
     */
    @Getter private BoundingBox enclosing;

    /**
     * Create with a box that has no padding.
     *
     * <p>i.e. this box is enclosed by itself.
     *
     * @param box the box.
     */
    public BoundingBoxEnclosed(BoundingBox box) {
        this.box = box;
        this.enclosing = box;
    }
}
