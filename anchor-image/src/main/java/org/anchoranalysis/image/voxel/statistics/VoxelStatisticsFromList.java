/* (C)2020 */
package org.anchoranalysis.image.voxel.statistics;

import org.anchoranalysis.bean.shared.relation.threshold.RelationToThreshold;
import org.anchoranalysis.core.relation.RelationToValue;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.voxel.VoxelIntensityList;

public class VoxelStatisticsFromList implements VoxelStatistics {

    private VoxelIntensityList pixelList;

    public VoxelStatisticsFromList(VoxelIntensityList pixelList) {
        super();
        this.pixelList = pixelList;
    }

    @Override
    public long size() {
        return pixelList.size();
    }

    @Override
    public long sum() {
        return (long) pixelList.sum();
    }

    @Override
    public long sumOfSquares() {
        return pixelList.sumOfSquares();
    }

    @Override
    public VoxelStatistics threshold(RelationToThreshold relationToThreshold) {
        VoxelIntensityList pixelListThresholded = thresholdPxlList(pixelList, relationToThreshold);
        return new VoxelStatisticsFromList(pixelListThresholded);
    }

    private static VoxelIntensityList thresholdPxlList(
            VoxelIntensityList list, RelationToThreshold relationToThreshold) {

        VoxelIntensityList pxlList = new VoxelIntensityList();

        RelationToValue relation = relationToThreshold.relation();
        double threshold = relationToThreshold.threshold();

        for (int i = 0; i < list.size(); i++) {
            double pxlVal = list.get(i);

            if (relation.isRelationToValueTrue(pxlVal, threshold)) {
                pxlList.add(pxlVal);
            }
        }
        return pxlList;
    }

    @Override
    public long countThreshold(RelationToThreshold relationToThreshold) {

        RelationToValue relation = relationToThreshold.relation();
        double threshold = relationToThreshold.threshold();

        long count = 0;

        for (int i = 0; i < pixelList.size(); i++) {
            double pxlVal = pixelList.get(i);

            if (relation.isRelationToValueTrue(pxlVal, threshold)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public double quantile(double quantile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Histogram histogram() {
        throw new UnsupportedOperationException();
    }
}
