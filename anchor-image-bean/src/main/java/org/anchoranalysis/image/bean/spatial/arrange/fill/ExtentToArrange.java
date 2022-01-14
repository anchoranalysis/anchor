package org.anchoranalysis.image.bean.spatial.arrange.fill;

import lombok.Getter;
import org.anchoranalysis.spatial.box.Extent;

/**
 * A particular {@link Extent} to be arranged, with calculated aspect-ratio, and its unique index
 * position.
 */
class ExtentToArrange {

    /** Index position. */
    @Getter private final int index;

    /** The {@link Extent}. */
    @Getter private Extent extent;

    /** The aspect-ratio: width of {@code extent} divided by height. */
    @Getter private final double aspectRatio;

    /**
     * Create with a particular index and size.
     *
     * @param index the index (from zero upwards).
     * @param extent the size.
     */
    public ExtentToArrange(int index, Extent extent) {
        this.index = index;
        this.extent = extent;
        this.aspectRatio = extent.aspectRatioXY();
    }

    /**
     * Changes the size of the image to match a row that has a total width of {@code rowWidth},
     * preserving aspect-ratio.
     *
     * @param sumAspectRatios the sum of the aspect-ratios of all images in the row, including the
     *     current image.
     * @param rowWidth the desired eventual width of the entire row of images.
     */
    public void scaleToMatchRow(double sumAspectRatios, double rowWidth) {
        double scaleFactor = extractScaleFactor(sumAspectRatios, rowWidth);
        this.extent = extent.scaleXYBy(scaleFactor, false);
    }

    /** Determines the scaling factor to use, to achieve a particular {@code targetWidth}. */
    private double extractScaleFactor(double sumAspectRatios, double rowWidth) {
        double widthPercent = aspectRatio / sumAspectRatios;
        return (widthPercent * rowWidth) / extent.x();
    }
}
