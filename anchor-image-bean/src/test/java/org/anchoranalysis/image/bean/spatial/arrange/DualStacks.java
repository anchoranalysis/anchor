package org.anchoranalysis.image.bean.spatial.arrange;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.spatial.box.Extent;

/** Two {@link RGBStack} of different sizes, with specific respective colors. */
class DualStacks {

    /** The size of the <i>big</i> stack. */
    public static final Extent SIZE_BIG = new Extent(60, 50, 30);

    /**
     * The size of the <i>small</i> stack. This covers more than half of {@code SIZE_BIG} in the X
     * and Y dimensions but not in the Z.
     */
    public static final Extent SIZE_SMALL = new Extent(35, 30, 30);

    /**
     * Create a list containing the two {@link RGBStack}s.
     *
     * @param colorBig the color of all voxels in the <i>big</i> stack.
     * @param colorSmall the color of all voxels in the <i>small</i> stack.
     * @param flattenSmall if true, the smaller stack is flattened in the z-dimension to be a single
     *     z-slice instead of multiple slices.
     * @return a newly created list, containing the two respective (newly created) big and small
     *     {@link RGBStack}s.
     */
    public static List<RGBStack> asList(
            RGBColor colorBig, RGBColor colorSmall, boolean flattenSmall) {

        RGBStack big = new RGBStack(SIZE_BIG, colorBig);
        RGBStack small = new RGBStack(sizeSmall(flattenSmall), colorSmall);

        return Arrays.asList(big, small);
    }

    /** The size of the small image, depending on whether the z-dimension is flattened or not. */
    private static Extent sizeSmall(boolean flattenZ) {
        if (flattenZ) {
            return SIZE_SMALL.flattenZ();
        } else {
            return SIZE_SMALL;
        }
    }
}
