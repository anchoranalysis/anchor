package org.anchoranalysis.image.bean.spatial.arrange.fill;

import lombok.Getter;
import lombok.ToString;
import org.anchoranalysis.spatial.box.Extent;

/**
 * A particular {@link Extent} to be arranged, with calculated aspect-ratio, and its unique index
 * position.
 *
 * <p>An equality check and ordering is imposed only in terms of the X-size of the {@code extent},
 * so as to sort in descending order by width.
 */
@ToString
public class ExtentToArrange implements Comparable<ExtentToArrange> {

    /** Index position. */
    @Getter private final int index;

    /** The {@link Extent}. */
    @Getter private Extent extent;

    /** The aspect-ratio: width of {@code extent} divided by height. */
    @Getter private final double aspectRatio;

    /**
     * Stores the difference that occurs between a discretized scaled width and what it should
     * ideally be.
     *
     * <p>This value will always lie in the range (-1,1) and a positive value means the discretized
     * version rounded down by the value, and a negative value means the discretized version rounded
     * up by the value.
     */
    @Getter private double scaleRoundingError = 0.0;

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
     * @return int the final selected width;
     */
    public int scaleToMatchRow(double sumAspectRatios, int rowWidth) {
        double scaleFactor = extractScaleFactor(sumAspectRatios, rowWidth);

        Extent extentToAssign = extent.scaleXYBy(scaleFactor, true);

        // Record the rounding difference that occurs with the width comparing to the ideal size
        // so that potentially we can sort by this later
        this.scaleRoundingError = (scaleFactor * extent.x()) - extentToAssign.x();
        this.extent = extentToAssign;
        return extent.x();
    }

    /**
     * Changes the width of the underlying {@link Extent} by adding {@code growBy} pixels.
     *
     * @param growBy how many pixels are added to the width of the existing {@link Extent}. This may
     *     also be negative.
     */
    public void growWidth(int growBy) {
        this.extent = extent.duplicateChangeX(extent.x() + growBy);
    }

    /**
     * The size in the X-dimension.
     *
     * @return the width.
     */
    public int width() {
        return extent.x();
    }

    /**
     * Returns the ratio of *image height to width*.
     *
     * <p>This is the inversion of the aspect-ratio, which is the ratio of *image width to height*.
     *
     * @return the inverted aspect-ratio.
     */
    public double aspectRatioInverted() {
        return 1.0 / aspectRatio;
    }

    @Override
    public int compareTo(ExtentToArrange o) {
        return -1 * extent.compareTo(o.extent);
    }

    /** Determines the scaling factor to use, to achieve a particular {@code targetWidth}. */
    private double extractScaleFactor(double sumAspectRatios, int rowWidth) {
        double widthPercent = aspectRatio / sumAspectRatios;
        return (widthPercent * rowWidth) / extent.x();
    }
}
