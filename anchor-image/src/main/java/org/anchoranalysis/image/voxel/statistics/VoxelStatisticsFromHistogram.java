/* (C)2020 */
package org.anchoranalysis.image.voxel.statistics;

import org.anchoranalysis.bean.shared.relation.threshold.RelationToThreshold;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.histogram.Histogram;

public class VoxelStatisticsFromHistogram implements VoxelStatistics {

    private Histogram histogram;

    public VoxelStatisticsFromHistogram(Histogram histogram) {
        super();
        this.histogram = histogram;
    }

    @Override
    public long size() {
        return histogram.getTotalCount();
    }

    @Override
    public long sum() {
        return histogram.calcSum();
    }

    @Override
    public long sumOfSquares() {
        return histogram.calcSumSquares();
    }

    @Override
    public VoxelStatistics threshold(RelationToThreshold relationToThreshold) {
        throw new UnsupportedOperationException(
                "The threshold operation is not currently supported");
    }

    @Override
    public long countThreshold(RelationToThreshold relationToThreshold) {
        return histogram.countThreshold(relationToThreshold);
    }

    @Override
    public double quantile(double quantile) throws OperationFailedException {
        return histogram.quantile(quantile);
    }

    @Override
    public Histogram histogram() {
        return histogram;
    }
}
