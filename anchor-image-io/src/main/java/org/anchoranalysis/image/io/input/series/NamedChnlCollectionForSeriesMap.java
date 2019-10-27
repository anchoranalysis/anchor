package org.anchoranalysis.image.io.input.series;

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


import java.util.Set;

import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.core.progress.ProgressReporterOneOfMany;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.chnl.map.ImgChnlMap;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;

public class NamedChnlCollectionForSeriesMap extends NamedChnlCollectionForSeries {

	private ImgChnlMap chnlMap;

	// Null until the first time we request a channel
	private OpenedRaster openedRaster;
	private TimeSequence ts = null;
	private int seriesNum;
	
	public NamedChnlCollectionForSeriesMap(OpenedRaster openedRaster, ImgChnlMap chnlMap, int seriesNum) {
		super();
		assert( chnlMap!=null );
		this.chnlMap = chnlMap;
		this.seriesNum = seriesNum;
		this.openedRaster = openedRaster;
	}
	
	private TimeSequence createTs( ProgressReporter progressReporter ) throws RasterIOException {
		if( ts==null) {
			// TODO create another way of inserting scaling information from the getChnlMap()
			ts = openedRaster.open(seriesNum, progressReporter );
		}
		return ts;
	}
	
	@Override
	public ImageDim dimensions() throws RasterIOException {
		return openedRaster.dim(seriesNum);
	}
			
	// The outputManager is in case we want to do any debugging
	public Chnl getChnl(String chnlName, int t, ProgressReporter progressReporter) throws RasterIOException {

		int index = chnlMap.get(chnlName);
		if (index==-1) {
			throw new RasterIOException( String.format("'%s' cannot be found", chnlName) );
		}
		
		Stack stack = createTs( progressReporter ).get(t); 

		if (index>=stack.getNumChnl()) {
			throw new RasterIOException( String.format("Stack does not have a channel corresponding to '%s'",chnlName) );
		}
		
		return stack.getChnl( chnlMap.getException(chnlName) );
	}
	
	// The outputManager is in case we want to do any debugging
	@Override
	public Chnl getChnlOrNull(String chnlName, int t, ProgressReporter progressReporter) throws RasterIOException {

		int index = chnlMap.get(chnlName);
		if (index==-1) {
			return null;
		}
		
		Stack stack = createTs( progressReporter ).get(t); 

		if (index>=stack.getNumChnl()) {
			return null;
		}
		
		return stack.getChnl( index );
	}
	
	@Override
	public int sizeT( ProgressReporter progressReporter ) throws RasterIOException {
		return createTs( progressReporter ).size();
	}

	@Override
	public Set<String> chnlNames() {
		return chnlMap.keySet();
	}
	

	@Override
	public boolean hasChnl(String chnlName) {
		return chnlMap.keySet().contains(chnlName);
	}
	
	@Override
	public void addToStackCollection( NamedImgStackCollection stackCollection, int t, ProgressReporter progressReporter ) throws RasterIOException {
		
		try {
			try( ProgressReporterMultiple prm = new ProgressReporterMultiple(progressReporter, chnlMap.keySet().size() )) {
		
				// Populate our stack from all the channels
				for (String chnlName : chnlMap.keySet() ) {
					Chnl image = getChnl(chnlName,  t, new ProgressReporterOneOfMany(prm) );
					stackCollection.addImageStack( chnlName, new Stack(image) );
					prm.incrWorker();
				}
			}
			
		} finally {
			progressReporter.close();
		}
	}

	@Override
	public void addToStackCollection( NamedProviderStore<TimeSequence> stackCollection, final int t ) throws OperationFailedException  {
		// Populate our stack from all the channels
		for (final String chnlName : chnlMap.keySet() ) {
			
			Operation<TimeSequence> op = new CachedOperation<TimeSequence>() {

				@Override
				protected TimeSequence execute() throws ExecuteException {
					Chnl image;
					try {
						image = getChnl(chnlName,  t, ProgressReporterNull.get());
					} catch (RasterIOException e) {
						throw new ExecuteException(e);
					}
					return new TimeSequence( new Stack(image) );
				}

	
			}; 
			stackCollection.add( chnlName, op );
		}
	}

	protected ImgChnlMap getChnlMap() {
		return chnlMap;
	}
}
