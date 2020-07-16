/* (C)2020 */
package org.anchoranalysis.image.voxel.statistics;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.bean.shared.relation.threshold.RelationToThreshold;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.histogram.HistogramArray;

public class VoxelStatisticsCombined implements VoxelStatistics {

    private List<VoxelStatistics> list = new ArrayList<>();

    public void add(VoxelStatistics ps) {
        list.add(ps);
    }

    @Override
    public long size() {
        int size = 0;
        for (VoxelStatistics ps : list) {
            size += ps.size();
        }
        return size;
    }

    @Override
    public long sum() {
        int sum = 0;
        for (VoxelStatistics ps : list) {
            sum += ps.sum();
        }
        return sum;
    }

    @Override
    public long sumOfSquares() {
        long cnt = 0;
        for (VoxelStatistics ps : list) {
            cnt += ps.sumOfSquares();
        }
        return cnt;
    }

    @Override
    public VoxelStatistics threshold(RelationToThreshold relationToThreshold) {

        VoxelStatisticsCombined out = new VoxelStatisticsCombined();
        for (VoxelStatistics ps : list) {
            out.add(ps.threshold(relationToThreshold));
        }
        return out;
    }

    @Override
    public long countThreshold(RelationToThreshold relationToThreshold) {
        long cnt = 0;
        for (VoxelStatistics ps : list) {
            cnt += ps.countThreshold(relationToThreshold);
        }
        return cnt;
    }

    @Override
    public double quantile(double quantile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Histogram histogram() throws OperationFailedException {

        Histogram out = new HistogramArray(255);
        for (VoxelStatistics ps : list) {
            out.addHistogram(ps.histogram());
        }
        return out;
    }
}
