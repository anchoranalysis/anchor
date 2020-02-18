package org.anchoranalysis.image.io.input;

/*
 * #%L
 * anchor-image-io
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
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.input.series.NamedChnlCollectionForSeries;
import org.anchoranalysis.image.stack.TimeSequence;

/**
 * Provides a set of channels as an input, each of which has a name. Only a single time-point is possible
 * 
 * @author Owen Feehan
 *
 */
public abstract class NamedChnlsInput extends ProvidesStackInput {

	/** Number of series */
	public abstract int numSeries() throws RasterIOException;
	
	/** Dimensions of a particular series */
	public abstract ImageDim dim( int seriesIndex ) throws RasterIOException;
	
	/** Number of channels */
	public abstract int numChnl() throws RasterIOException;
	
	/** Bit-depth of image */
	public abstract int bitDepth() throws RasterIOException;
	
	// Where most of our time is being taken up when opening a raster
	public abstract NamedChnlCollectionForSeries createChnlCollectionForSeries( int seriesNum, ProgressReporter progressReporter ) throws RasterIOException;

	@Override
	public void addToStore(NamedProviderStore<TimeSequence> stackCollection, int seriesNum, ProgressReporter progressReporter)
			throws OperationFailedException {

		try {
			NamedChnlCollectionForSeries ncc = createChnlCollectionForSeries(seriesNum, progressReporter);
			// Apply it only to first time-series frame
			ncc.addToStackCollection(stackCollection, 0);
			
		} catch (RasterIOException e) {
			throw new OperationFailedException(e);
		}
		
	}
	
	@Override
	public void addToStoreWithName(String name,
			NamedProviderStore<TimeSequence> stackCollection, int seriesNum, ProgressReporter progressReporter) throws OperationFailedException {
		// We ignore the name
		addToStore(stackCollection, seriesNum, progressReporter);
	}

	@Override
	public int numFrames() {
		return 1;
	}

}
