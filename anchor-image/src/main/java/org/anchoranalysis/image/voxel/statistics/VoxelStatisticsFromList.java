package org.anchoranalysis.image.voxel.statistics;

/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.core.relation.RelationToValue;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.voxel.VoxelIntensityList;

public class VoxelStatisticsFromList extends VoxelStatistics {

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
	public VoxelStatistics threshold(RelationToValue relationToThreshold,
			double threshold) {
		VoxelIntensityList pixelListThresholded = thresholdPxlList(pixelList, relationToThreshold, threshold);
		return new VoxelStatisticsFromList(pixelListThresholded);
	}
		
	private static VoxelIntensityList thresholdPxlList( VoxelIntensityList list, RelationToValue relationToThreshold, double threshold ) {
		
		VoxelIntensityList pxlList = new VoxelIntensityList();
		
		for (int i=0; i<list.size(); i++) {
			double pxlVal = list.get(i);
			
			if (relationToThreshold.isRelationToValueTrue(pxlVal,threshold)) {
				pxlList.add(pxlVal);
			}
		}
		return pxlList;
	}
	
	@Override
	public long countThreshold( RelationToValue relationToThreshold, double threshold ) {
		long count = 0;
		
		for (int i=0; i<pixelList.size(); i++) {
			double pxlVal = pixelList.get(i);
			
			if (relationToThreshold.isRelationToValueTrue(pxlVal,threshold)) {
				count++;
			}
		}
		return count;
	}

	@Override
	public double quantile(double quantile) {
		// TODO Currently unsupported, we need to worry about sorted lists
		assert false;
		return -1;
	}

	@Override
	public Histogram histogram() {
		// Currently unsupported
		assert false;
		return null;
	}

	
}
