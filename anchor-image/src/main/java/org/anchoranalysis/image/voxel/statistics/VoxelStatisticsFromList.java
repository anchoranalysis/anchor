/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
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
