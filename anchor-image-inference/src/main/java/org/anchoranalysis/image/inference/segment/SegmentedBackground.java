package org.anchoranalysis.image.inference.segment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.image.bean.displayer.StackDisplayer;
import org.anchoranalysis.image.core.stack.Stack;

/**
 * The background to a segmentation.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class SegmentedBackground {

    /** The background image to use for segmentation, when visualizing segmentations. */
    @Getter private final DualScale<Stack> background;

    /** How to convert the background in an image suitable to be displayed. */
    @Getter private final StackDisplayer displayer;

    /**
     * The background scaled to match the size of the input-image.
     *
     * @return the element, at the requested scale.
     */
    public Stack atInputScale() {
        return background.atInputScale();
    }

    /**
     * The background scaled to match the size of the input for model inference.
     *
     * @return the element, at the requested scale.
     */
    public Stack atModelScale() {
        return background.atModelScale();
    }
}
