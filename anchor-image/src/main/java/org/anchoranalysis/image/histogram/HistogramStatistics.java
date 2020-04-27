package org.anchoranalysis.image.histogram;

import org.anchoranalysis.core.error.OperationFailedException;

/** 
 * Further statistics that can be derived from a histogram in addition to those callable directly from the {@link Histogram} class
 */
public class HistogramStatistics {
	
	private HistogramStatistics() {}
	
	public static double coefficientOfVariation( Histogram hist ) throws OperationFailedException {
		double mean = hist.mean();
		
		if (mean==0) {
			throw new OperationFailedException("The mean is 0 so the coefficient-of-variation is undefined");
		}
		
		return hist.stdDev() / mean;
	}
	
	public static double kurtosis(Histogram hist) throws OperationFailedException {

		// Kurtosis is calculated as in
		// http://www.macroption.com/kurtosis-formula/
		double histMean = hist.mean();
		
		double fourthMomentAboutMean = hist.mean(4.0, histMean);
		
		double varSquared = Math.pow( hist.variance(), 2.0 );
		
		if (varSquared==0) {
			// We don't return infinity, but rather the maximum value allowed
			throw new OperationFailedException("Kurtosis is undefined as there is 0 variance");
		}
		
		return fourthMomentAboutMean / varSquared;
	}
	
	public static double skewness(Histogram hist) throws OperationFailedException {

		long count = hist.getTotalCount();
		double mean = hist.mean();
		double sd = hist.stdDev();
		
		// Calculated using formula in https://en.wikipedia.org/wiki/Skewness
		long firstTerm = hist.calcSumCubes() / count;
		double secondTerm = -3.0 * mean * sd * sd;
		double thirdTerm = mean * mean * mean;
		
		double dem = sd * sd * sd;
		
		return (((double) firstTerm) + secondTerm + thirdTerm) / dem; 
	}
}
