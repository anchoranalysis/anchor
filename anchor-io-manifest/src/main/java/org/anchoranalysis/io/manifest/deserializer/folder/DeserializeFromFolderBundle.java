package org.anchoranalysis.io.manifest.deserializer.folder;

/*
 * #%L
 * anchor-io
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


import java.io.Serializable;

import org.anchoranalysis.core.cache.CacheMonitor;
import org.anchoranalysis.core.index.ITypedGetFromIndex;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.deserializer.folder.DeserializedObjectFromFolderBundle.BundleDeserializers;
import org.anchoranalysis.io.manifest.folder.FolderWrite;

public abstract class DeserializeFromFolderBundle<HistoryType,CacheType extends Serializable> extends HistoryCreator<HistoryType> {

	private final BundleDeserializers<CacheType> deserializers;
	private FolderWrite folder;
	private CacheMonitor cacheMonitor;
	
	public DeserializeFromFolderBundle( final BundleDeserializers<CacheType> deserializers, FolderWrite folder, CacheMonitor cacheMonitor ) {
		this.deserializers = deserializers;
		this.folder = folder;
		this.cacheMonitor = cacheMonitor;
	}
	
	@Override
	public LoadContainer<HistoryType> create() throws DeserializationFailedException {
		
		assert( folder.getManifestFolderDescription().getSequenceType() != null );
		
		int cacheSize = 5;
		
		DeserializedObjectFromFolderBundle<CacheType> deserializeFromBundle = new DeserializedObjectFromFolderBundle<>( folder, deserializers, cacheSize, cacheMonitor );

		IBoundedIndexContainer<HistoryType> boundedContainer = new BoundsFromSequenceType<>(
				createCntr(deserializeFromBundle),
				deserializeFromBundle.getBundleParameters().getSequenceType() 
			);
		
		
		LoadContainer<HistoryType> history = new LoadContainer<>();
		history.setCntr( boundedContainer );
		
		int numberOfBundles = folder.getManifestFolderDescription().getSequenceType().getNumElements();
		if ( numberOfBundles > cacheSize ) {
			// If we have more bundles than the size of the cache, then we leave adjusting mode off
			history.setExpensiveLoad(true);
		} else {
			// If our cache is big enough for all our bundles then we go into adjusting mode
			history.setExpensiveLoad(false);
		}
		//history.setExpensiveLoad(true);
		
		//assert( history.getCfgNRGCntnr().get(0)!=null );
		
		return history;
	}
	
	protected abstract ITypedGetFromIndex<HistoryType> createCntr( DeserializedObjectFromFolderBundle<CacheType> deserializeFromBundle);
}
