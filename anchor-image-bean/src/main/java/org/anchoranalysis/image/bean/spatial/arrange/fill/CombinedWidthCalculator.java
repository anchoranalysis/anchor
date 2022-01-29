package org.anchoranalysis.image.bean.spatial.arrange.fill;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Calculates the combined width for them ontage.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class CombinedWidthCalculator {

    /**
     * Calculates the width to use for the combined-image.
     *
     * <p>This will be either from {@code width} or calculated using the {@code widthRatio}.
     *
     * <p>If both are specified, the minimum is taken.
     *
     * @param partitions how the images are arranged in the table.
     * @param width a specific width if one when assigned, or 0 if unassigned.
     * @param widthRatio a portion of natural row-width when assigned, or 0.0 if unassigned.
     * @return the calculated combined width to use for partitions.
     */
    public static int calculate(
            List<List<ExtentToArrange>> partitions, int width, double widthRatio) {
        if (width > 0) {
            if (widthRatio > 0.0) {
                return Math.min(width, calculateFromRatio(partitions, widthRatio));
            } else {
                return width;
            }
        } else {
            // Then widthRatio must be assigned
            return calculateFromRatio(partitions, widthRatio);
        }
    }

    /** Calculates a target-width from the {@code widthRatio}. */
    private static int calculateFromRatio(
            List<List<ExtentToArrange>> partitions, double widthRatio) {
        return (int) Math.round(widthRatio * averageRowWidth(partitions));
    }

    /** Calculates the average row-width across all partitions. */
    private static double averageRowWidth(List<List<ExtentToArrange>> partitions) {
        return partitions.stream()
                .mapToInt(CombinedWidthCalculator::widthOfRow)
                .average()
                .getAsDouble(); // NOSONAR
    }

    /** Calculates the width of a particular row in the partition. */
    private static int widthOfRow(List<ExtentToArrange> extents) {
        return extents.stream().mapToInt(ExtentToArrange::width).sum();
    }
}
