package org.anchoranalysis.image.bean.spatial.arrange.align;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
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
    @Getter @Setter @BeanField private boolean preserveAspectRatio = false;
    // END BEAN PROPERTIES

    @Override
    public BoundingBox alignAfterCheck(BoundingBox smaller, BoundingBox larger) {
        if (preserveAspectRatio) {
            return alignPreserveAspectRatio(smaller, larger);
        } else {
            return larger;
        }
    }

    /**
     * Grow the bounding-box to fill {@code larger} as much as possible, preserving the aspect-ratio
     * of {@code smaller}.
     */
    private BoundingBox alignPreserveAspectRatio(BoundingBox smaller, BoundingBox larger) {
        ScaleFactor factor =
                RelativeScaleCalculator.relativeScalePreserveAspectRatio(
                        smaller.extent(), larger.extent());
        Extent imageScaled = smaller.extent().scaleXYBy(factor, true);
        return CenterUtilities.centerSmaller(smaller.cornerMin(), imageScaled, larger);
    }
}
