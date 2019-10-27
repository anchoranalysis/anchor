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


import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.relation.RelationToValue;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.math.statistics.VarianceCalculator;

// Allows retrieval of statistics in relation to pixels
public abstract class VoxelStatistics {

	public abstract long size();
	
	public abstract long sum();
	
	public abstract long sumOfSquares();
	
	public abstract VoxelStatistics threshold( RelationToValue relationToThreshold, double threshold );
	
	public abstract double quantile( double quantile );
	
	public abstract Histogram histogram() throws OperationFailedException;
	
	// Avoids the overhead with assigning new memory if we we are just counting
	public abstract long countThreshold( RelationToValue relationToThreshold, double threshold );

	public double variance() {
		return new VarianceCalculator( sum(), sumOfSquares(),  size() ).variance();
	}
	
	public double stdDev() {
		return Math.sqrt( variance() );
	}
	public double mean() {
		return ((double) sum()) / size();
	}
}
