package org.anchoranalysis.image.bean.spatial.arrange.align;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.scale.RelativeScaleCalculator;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * Grow the bounding-box to fill {@code larger} as much as possible.
 *
 * <p>Depending on {@code preserveAspectRatio}, the aspect-ratio of {@code smaller} is preserved or
 * not.
 *
 * <p>When {@code preserveAspectRatio==false}, the smaller bounding-box is guaranteed to become
 * identical to the larger.
 *
 * <p>Growth never occurs in the z-dimension, and the size in this dimension should be equal for
 * both {@code smaller} and {@code larger}.
 */
@NoArgsConstructor
@AllArgsConstructor
public class Grow extends BoxAligner {

    // START BEAN PROPERTIES
    /** Whether to preserve the aspect-ratio of the smaller image when growing. */
    @Getter @Setter @BeanField private boolean preserveAspectRatio = false;

    /** How to align the box after it is grown, as much as possible. */
    @Getter @Setter @BeanField private Align align = new Align();
    // END BEAN PROPERTIES

    /**
     * Create to preserve the aspect-ratio or not.
     *
     * @param preserveAspectRatio whether to preserve the aspect-ratio of the smaller image when
     *     growing.
     */
    public Grow(boolean preserveAspectRatio) {
        this.preserveAspectRatio = preserveAspectRatio;
    }

    @Override
    public BoundingBox alignAfterCheck(BoundingBox smaller, BoundingBox larger)
            throws OperationFailedException {
        if (preserveAspectRatio) {
            Extent smallerGrown = growSmaller(smaller.extent(), larger.extent());
            return align.align(smallerGrown, larger);
        } else {
            return larger;
        }
    }

    @Override
    public BoundingBox alignAfterCheck(Extent smaller, Extent larger)
            throws OperationFailedException {
        if (preserveAspectRatio) {
            Extent smallerGrown = growSmaller(smaller, larger);
            return align.align(smallerGrown, larger);
        } else {
            return new BoundingBox(larger);
        }
    }

    @Override
    public BoundingBox alignAfterCheck(Extent smaller, BoundingBox larger)
            throws OperationFailedException {
        if (preserveAspectRatio) {
            Extent smallerGrown = growSmaller(smaller, larger.extent());
            return align.align(smallerGrown, larger);
        } else {
            return larger;
        }
    }

    /**
     * Scales {@code smaller} as much as possible, preserving aspect-ratio, while still fitting
     * inside {@code larger}.
     */
    private Extent growSmaller(Extent smaller, Extent larger) {
        ScaleFactor factor =
                RelativeScaleCalculator.relativeScalePreserveAspectRatio(smaller, larger);
        return smaller.scaleXYBy(factor, true);
    }
}
