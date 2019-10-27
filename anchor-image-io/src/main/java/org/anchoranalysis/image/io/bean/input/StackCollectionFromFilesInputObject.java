package org.anchoranalysis.image.io.bean.input;

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


import java.io.File;
import java.nio.file.Path;

import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.input.StackSequenceInput;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.io.input.FileInput;

class StackCollectionFromFilesInputObject extends StackSequenceInput {

	private FileInput delegate;
	private RasterReader rasterReader;
	
	// We cache a certain amount of stacks read for particular series
	private OpenedRaster openedRasterMemo = null;
	
	// This is to correct for a problem with formats such as czi where the seriesIndex doesn't indicate
	//   the total number of series but rather is incremented with each acquisition, so for our purposes
	//   we treat it as if its 0
	private boolean useLastSeriesIndexOnly = false;

	// The root object that is used to provide the descriptiveName and pathForBinding
	public StackCollectionFromFilesInputObject(FileInput delegate, RasterReader rasterReader, boolean useLastSeriesIndexOnly) {
		super();
		assert( rasterReader!=null );
		this.delegate = delegate;
		this.rasterReader = rasterReader;
		this.useLastSeriesIndexOnly = useLastSeriesIndexOnly;
	}

	public int numSeries() throws RasterIOException {
		if (useLastSeriesIndexOnly) {
			return 1;
		} else {
			return getOpenedRaster().getNumSeries();
		}
	}
		
	@Override
	public int numFrames() throws OperationFailedException {
		
		try {
			return getOpenedRaster().getNumFrames();
		} catch (RasterIOException e) {
			throw new OperationFailedException(e);
		}
	}
	
	
	public OperationWithProgressReporter<TimeSequence> createStackSequenceForSeries( int seriesNum ) throws RasterIOException {
		
		// We always use the last one
		if (useLastSeriesIndexOnly) {
			seriesNum = getOpenedRaster().getNumSeries()-1;
		}
		return openRasterAsOperation( getOpenedRaster(), seriesNum);
	}
	

	@Override
	public void addToStore(NamedProviderStore<TimeSequence> stackCollection,
			int seriesNum, ProgressReporter progressReporter)
			throws OperationFailedException {
		throw new OperationFailedException("Not supported");
	}
	

	@Override
	public void addToStoreWithName(String name,
			NamedProviderStore<TimeSequence> stackCollection, int seriesNum, ProgressReporter progressReporter) throws OperationFailedException {
	
		Operation<TimeSequence> opGetTimeSpecificStack = () -> {
			try {
				return createStackSequenceForSeries(seriesNum).doOperation(progressReporter);
			} catch (RasterIOException e) {
				throw new ExecuteException(e);
			}
		};
		stackCollection.add(name, opGetTimeSpecificStack);
	}

	
	private static OperationWithProgressReporter<TimeSequence> openRasterAsOperation( final OpenedRaster openedRaster, final int seriesNum ) {
		
		return new OperationWithProgressReporter<TimeSequence>() {

			@Override
			public TimeSequence doOperation( ProgressReporter progressReporter ) throws ExecuteException {
				try {
					return openedRaster.open(seriesNum, progressReporter );
				} catch (RasterIOException e) {
					throw new ExecuteException(e);
				}
			}
				
		};
	}

	@Override
	public String descriptiveName() {
		return delegate.descriptiveName();
	}

	@Override
	public Path pathForBinding() {
		return delegate.pathForBinding();
	}

	public RasterReader getRasterReader() {
		return rasterReader;
	}

	public File getFile() {
		return delegate.getFile();
	}
	
	private OpenedRaster getOpenedRaster() throws RasterIOException {
		if (openedRasterMemo==null) {
			openedRasterMemo = rasterReader.openFile( delegate.pathForBinding() );
		}
		return openedRasterMemo;
	}

	@Override
	public void close(ErrorReporter errorReporter) {
		if (openedRasterMemo!=null) {
			try {
				openedRasterMemo.close();
			} catch (RasterIOException e) {
				errorReporter.recordError(StackSequenceInput.class, e);
			}
		}
	}

}
