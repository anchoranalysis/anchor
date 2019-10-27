package org.anchoranalysis.image.voxel.statistics;

/*
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.relation.RelationToValue;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.histogram.HistogramArray;

public class VoxelStatisticsCombined extends VoxelStatistics {

	private List<VoxelStatistics> list = new ArrayList<>();

	public void add( VoxelStatistics ps ) {
		list.add(ps);
	}
	
	@Override
	public long size() {
		int size = 0;
		for( VoxelStatistics ps : list) {
			size += ps.size();
		}
		return size;
	}

	@Override
	public long sum() {
		int sum = 0;
		for( VoxelStatistics ps : list) {
			sum += ps.sum();
		}
		return sum;
	}

	@Override
	public long sumOfSquares() {
		long cnt = 0;
		for( VoxelStatistics ps : list) {
			cnt += ps.sumOfSquares();
		}
		return cnt;
	}

	@Override
	public VoxelStatistics threshold(RelationToValue relationToThreshold,
			double threshold) {

		VoxelStatisticsCombined out = new VoxelStatisticsCombined();
		for( VoxelStatistics ps : list) {
			out.add( ps.threshold(relationToThreshold, threshold) );
		}
		return out;
	}

	@Override
	public long countThreshold(RelationToValue relationToThreshold, double threshold) {
		long cnt = 0;
		for( VoxelStatistics ps : list) {
			cnt += ps.countThreshold(relationToThreshold, threshold);
		}
		return cnt;
	}

	@Override
	public double quantile(double quantile) {
		// TODO currently unspported
		assert false;
		return -1;
	}

	@Override
	public Histogram histogram() throws OperationFailedException {

		Histogram out = new HistogramArray(255);
		for( VoxelStatistics ps : list) {
			out.addHistogram( ps.histogram() );
		}
		return out;
	}


}
