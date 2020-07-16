/* (C)2020 */
package org.anchoranalysis.image.voxel.statistics;

import org.anchoranalysis.bean.shared.relation.threshold.RelationToThreshold;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.math.statistics.VarianceCalculator;

/**
 * Allows retrieval of statistics in relation to pixels
 *
 * @author Owen Feehan
 */
public interface VoxelStatistics {

    long size();

    long sum();

    long sumOfSquares();

    VoxelStatistics threshold(RelationToThreshold relationToThreshold);

    double quantile(double quantile) throws OperationFailedException;

    Histogram histogram() throws OperationFailedException;

    // Avoids the overhead with assigning new memory if we we are just counting
    long countThreshold(RelationToThreshold relationToThreshold);

    default double variance() {
        return new VarianceCalculator(sum(), sumOfSquares(), size()).variance();
    }

    default double stdDev() {
        return Math.sqrt(variance());
    }

    default double mean() {
        return ((double) sum()) / size();
    }
}
