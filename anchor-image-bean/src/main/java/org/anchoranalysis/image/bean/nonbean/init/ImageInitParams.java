package org.anchoranalysis.image.bean.nonbean.init;

/*-
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import java.nio.file.Path;

import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.init.params.BeanInitParams;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.bean.store.BeanStoreAdder;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.functional.IdentityOperation;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.image.bean.provider.BinaryChnlProvider;
import org.anchoranalysis.image.bean.provider.ChnlProvider;
import org.anchoranalysis.image.bean.provider.HistogramProvider;
import org.anchoranalysis.image.bean.provider.ObjectCollectionProvider;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.bean.segmentation.binary.BinarySegmentation;
import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.stack.Stack;

import lombok.Getter;

// A wrapper around SharedObjects which types certain Image entities
public class ImageInitParams implements BeanInitParams {

	@Getter
	private final SharedObjects sharedObjects;
	
	// START: InitParams
	private final KeyValueParamsInitParams soParams;
	private final SharedFeaturesInitParams soFeature;
	// END: InitParams
	
	// START: Stores
	private final NamedProviderStore<Stack> storeStack;
	private final NamedProviderStore<Histogram> storeHistogram;
	private final NamedProviderStore<ObjectCollection> storeObjMaskCollection;
	private final NamedProviderStore<Channel> storeChnl;
	private final NamedProviderStore<BinaryChnl> storeBinaryChnl;
	private final NamedProviderStore<BinarySegmentation> storeBinarySgmn;
	// END: Stores
	
	private FunctionWithException<StackProvider,Stack,OperationFailedException> stackProviderBridge;
	
	
	public ImageInitParams(SharedObjects sharedObjects) {
		super();
		this.sharedObjects = sharedObjects;
		this.soParams = KeyValueParamsInitParams.create(sharedObjects);
		this.soFeature = SharedFeaturesInitParams.create(sharedObjects);
		
		storeStack = sharedObjects.getOrCreate(Stack.class);
		storeHistogram = sharedObjects.getOrCreate(Histogram.class);
		storeObjMaskCollection = sharedObjects.getOrCreate(ObjectCollection.class);
		storeChnl = sharedObjects.getOrCreate(Channel.class);
		storeBinaryChnl = sharedObjects.getOrCreate(BinaryChnl.class);
		storeBinarySgmn = sharedObjects.getOrCreate(BinarySegmentation.class);
	}
	
	public NamedProviderStore<Stack> getStackCollection() {
		return storeStack;
	}
	
	public NamedProviderStore<Histogram> getHistogramCollection() {
		return storeHistogram;
	}
	
	public NamedProviderStore<ObjectCollection> getObjMaskCollection() {
		return storeObjMaskCollection;
	}
	
	public NamedProviderStore<Channel> getChnlCollection() {
		return storeChnl;
	}
	
	public NamedProviderStore<BinaryChnl> getBinaryImageCollection() {
		return storeBinaryChnl;
	}
	
	public NamedProviderStore<BinarySegmentation> getBinarySgmnSet() {
		return storeBinarySgmn;
	}

	public KeyValueParamsInitParams getParams() {
		return soParams;
	}

	public SharedFeaturesInitParams getFeature() {
		return soFeature;
	}
	
	public void populate( PropertyInitializer<?> pi, Define define, Logger logger ) throws OperationFailedException {
		
		soFeature.populate(
			define.getList(FeatureListProvider.class),
			logger
		);
		
		PopulateStoreFromDefine<ImageInitParams> populate = new PopulateStoreFromDefine<>(define, pi, logger);
		
		populate.copyInit(BinarySegmentation.class, getBinarySgmnSet());
		populate.copyProvider(BinaryChnlProvider.class, getBinaryImageCollection());
		populate.copyProvider(ChnlProvider.class, getChnlCollection());
		populate.copyProvider(ObjectCollectionProvider.class, getObjMaskCollection());
		populate.copyProvider(HistogramProvider.class, getHistogramCollection());
		
		stackProviderBridge = populate.copyProvider(StackProvider.class, getStackCollection());
	}
	
	public void addToStackCollection(String identifier, Stack inputImage) throws OperationFailedException {
		getStackCollection().add(identifier, new IdentityOperation<>(inputImage));
	}
	
	public void addToStackCollection(String identifier, StackProvider stackProvider ) throws OperationFailedException {
		BeanStoreAdder.add(identifier, stackProvider, getStackCollection(), stackProviderBridge);
	}
	
	public void copyStackCollectionFrom( NamedProvider<Stack> stackCollectionSource ) throws OperationFailedException {

		try {
			for (String id : stackCollectionSource.keys()) {
				Stack stack = stackCollectionSource.getException(id);
				if (stack!=null) {
					addToStackCollection(id,stack);
				}
			}
		} catch (NamedProviderGetException e) {
			throw new OperationFailedException(e.summarize());
		}
	}
	
	public void copyObjMaskCollectionFrom( NamedProvider<ObjectCollection> collectionSource ) throws OperationFailedException {

		try {
			for (String id : collectionSource.keys()) {
				ObjectCollection objs = collectionSource.getException(id);
				if (objs!=null) {
					addToObjMaskCollection(id, new IdentityOperation<>(objs) );
				}
			}
		} catch (NamedProviderGetException e) {
			throw new OperationFailedException(e.summarize());
		}
	}
	
	public void addToObjMaskCollection(String identifier, Operation<ObjectCollection,OperationFailedException> opObjMaskCollection) throws OperationFailedException {
		getObjMaskCollection().add(identifier, opObjMaskCollection);
	}
	
	public void addToKeyValueParamsCollection( String identifier, KeyValueParams params ) throws OperationFailedException {
		getParams().getNamedKeyValueParamsCollection().add(
			identifier,
			new IdentityOperation<>(params)
		);
	}

	public Path getModelDirectory() {
		return sharedObjects.getContext().getModelDirectory();
	}
}
