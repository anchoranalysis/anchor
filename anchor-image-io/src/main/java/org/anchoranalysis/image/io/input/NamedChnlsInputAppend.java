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


import java.io.File;
import java.nio.Buffer;
import java.nio.file.Path;
import java.util.List;

import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.input.series.NamedChnlCollectionForSeries;
import org.anchoranalysis.image.io.input.series.NamedChnlCollectionForSeriesConcatenate;
import org.anchoranalysis.image.io.input.series.NamedChnlCollectionForSeriesMap;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;

/**
 * Appends another channel to an existing NamedChnlInputBase
 * 
 * @author Owen Feehan
 *
 * @param <T> voxel data-type buffer
 */
public class NamedChnlsInputAppend<T extends Buffer> extends NamedChnlsInputBase {

	private NamedChnlsInputBase delegate;
	private AdditionalChnl additionalChnl;
	private RasterReader rasterReader;
	
	private OpenedRaster openedRasterMemo;
	
	public NamedChnlsInputAppend(
			NamedChnlsInputBase delegate,
			String chnlName, int chnlIndex, Operation<Path> filePath, RasterReader rasterReader ) {
		super();
		this.delegate = delegate;
		this.additionalChnl = new AdditionalChnl(chnlName, chnlIndex, filePath);
		this.rasterReader = rasterReader;
	}

	@Override
	public int numSeries() throws RasterIOException {
		return delegate.numSeries();
	}
	

	@Override
	public ImageDim dim(int seriesIndex) throws RasterIOException {
		return delegate.dim(seriesIndex);
	}

	@Override
	public boolean hasChnl(String chnlName) throws RasterIOException {
		
		if (additionalChnl.getChnlName().equals(chnlName)) {
			return true;
		}
		return delegate.hasChnl(chnlName);
	}

	@Override
	public NamedChnlCollectionForSeries createChnlCollectionForSeries(
			int seriesNum, ProgressReporter progressReporter) throws RasterIOException {
		
		NamedChnlCollectionForSeries exst = delegate.createChnlCollectionForSeries(seriesNum, progressReporter );
		
		openRasterIfNecessary();
		
		NamedChnlCollectionForSeriesConcatenate<T> out = new NamedChnlCollectionForSeriesConcatenate<T>();
		out.add( exst );
		out.add( new NamedChnlCollectionForSeriesMap(
			openedRasterMemo,
			additionalChnl.createChnlMap(),
			seriesNum )
		);
		return out;
	}
	
	private void openRasterIfNecessary() throws RasterIOException {
		try {
			Path filePathAdditional = additionalChnl.getFilePath();
			
			if (openedRasterMemo==null) {
				openedRasterMemo = rasterReader.openFile( filePathAdditional );
			}
			
		} catch (GetOperationFailedException e) {
			throw new RasterIOException(e);
		}
	}

	@Override
	public String descriptiveName() {
		return delegate.descriptiveName();
	}
	
	@Override
	public List<Path> pathForBindingForAllChannels() throws GetOperationFailedException {
		
		List<Path> list = delegate.pathForBindingForAllChannels();
		list.add( additionalChnl.getFilePath() );
		return list;
	}

	@Override
	public Path pathForBinding() {
		return delegate.pathForBinding();
	}

	@Override
	public File getFile() {
		return delegate.getFile();
	}

	@Override
	public int numChnl() throws RasterIOException {
		return delegate.numChnl();
	}

	@Override
	public int bitDepth() throws RasterIOException {
		return delegate.bitDepth();
	}
	
	@Override
	public void close(ErrorReporter errorReporter) {
		if (openedRasterMemo!=null) {
			try {
				openedRasterMemo.close();
			} catch (RasterIOException e) {
				errorReporter.recordError(NamedChnlsInputAppend.class, e);
			}
		}
		delegate.close(errorReporter);
	}
}
