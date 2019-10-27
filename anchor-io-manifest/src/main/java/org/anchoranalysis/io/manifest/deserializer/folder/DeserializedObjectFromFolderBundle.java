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
import java.util.HashMap;

import org.anchoranalysis.core.cache.CacheMonitor;
import org.anchoranalysis.core.cache.LRUHashMapCache;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.ITypedGetFromIndex;
import org.anchoranalysis.core.index.container.IOrderProvider;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.deserializer.bundle.Bundle;
import org.anchoranalysis.io.manifest.deserializer.bundle.BundleParameters;
import org.anchoranalysis.io.manifest.deserializer.bundle.BundleUtilities;
import org.anchoranalysis.io.manifest.folder.FolderWrite;

public class DeserializedObjectFromFolderBundle<T extends Serializable> implements ITypedGetFromIndex<T> {

	private LRUHashMapCache<HashMap<Integer,T>,Integer> cache;
	private BundleParameters bundleParameters;
	private IOrderProvider orderProvider;
	
	public static class BundleDeserializers<T extends Serializable> {
		private Deserializer<Bundle<T>> deserializerBundle;
		private Deserializer<BundleParameters> deserializerBundleParameters;
		
		public BundleDeserializers(Deserializer<Bundle<T>> deserializerBundle,
				Deserializer<BundleParameters> deserializerBundleParameters) {
			super();
			this.deserializerBundle = deserializerBundle;
			this.deserializerBundleParameters = deserializerBundleParameters;
		}

		public Deserializer<Bundle<T>> getDeserializerBundle() {
			return deserializerBundle;
		}

		public Deserializer<BundleParameters> getDeserializerBundleParameters() {
			return deserializerBundleParameters;
		}
	
	}
	
	public DeserializedObjectFromFolderBundle(FolderWrite folderWrite, final BundleDeserializers<T> deserializers, int cacheSize, CacheMonitor cacheMonitor ) throws DeserializationFailedException {
		super();
		
		final FolderWrite bundleFolder = folderWrite;
		
		// We create our cache
		this.cache = LRUHashMapCache.createAndMonitor(
			cacheSize,
			new LRUHashMapCache.Getter<HashMap<Integer,T>,Integer>() {
				
				@Override
				public HashMap<Integer, T> get(Integer index) throws GetOperationFailedException {
					Bundle<T> bundle;
					try {
						bundle = BundleUtilities.generateBundle( deserializers.getDeserializerBundle(), bundleFolder, index);
					} catch (IllegalArgumentException e) {
						throw new GetOperationFailedException(e);
					} catch (DeserializationFailedException e) {
						throw new GetOperationFailedException(e);
					}
					return bundle.createHashMap();
				}
			},
			cacheMonitor,
			"DeserializedObjectFromFolderBundle"
		);
		
		bundleParameters = BundleUtilities.generateBundleParameters( deserializers.getDeserializerBundleParameters(), folderWrite );
		
		if (bundleParameters==null) {
			throw new DeserializationFailedException("Cannot find bundle parameters");
		}
		
		orderProvider = bundleParameters.getSequenceType().createOrderProvider();

	}

	@Override
	public T get(int index) throws GetOperationFailedException {
		
		// We divide the index by the bundle size, to get whichever bundle we need to retrieve
		int bundleIndex = orderProvider.order( String.valueOf(index) ) / bundleParameters.getBundleSize();
		
		// This HashMap can contain NULL keys representing deliberately null objects
		HashMap<Integer,T> hashMap = this.cache.get(bundleIndex);
		
		if (!hashMap.containsKey(index)) {
			throw new GetOperationFailedException( String.format("Cannot find index %i in bundle",index) );
		}
		
		T obj = hashMap.get( index );
		return obj;
	}

	public BundleParameters getBundleParameters() {
		return bundleParameters;
	}

}
