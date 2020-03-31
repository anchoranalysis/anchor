package org.anchoranalysis.image.init;

import java.nio.file.Path;

import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.init.params.BeanInitParams;
import org.anchoranalysis.bean.init.params.IdentityBridgeInit;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.bean.store.BeanStoreAdder;

/*
 * #%L
 * anchor-image-bean
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


import org.anchoranalysis.core.cache.IdentityOperation;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.core.random.RandomNumberGeneratorMersenneTime;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.image.bean.provider.BinaryImgChnlProvider;
import org.anchoranalysis.image.bean.provider.ChnlProvider;
import org.anchoranalysis.image.bean.provider.HistogramProvider;
import org.anchoranalysis.image.bean.provider.ObjMaskProvider;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.bean.sgmn.binary.BinarySgmn;
import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.provider.ProviderBridge;
import org.anchoranalysis.image.stack.Stack;

// A wrapper around SharedObjects which types certain Image entities
public class ImageInitParams extends BeanInitParams {

	// START: InitParams
	private KeyValueParamsInitParams soParams;
	private SharedFeaturesInitParams soFeature;
	// END: InitParams
	
	// START: Stores
	private NamedProviderStore<Stack> storeStack;
	private NamedProviderStore<Histogram> storeHistogram;
	private NamedProviderStore<ObjMaskCollection> storeObjMaskCollection;
	private NamedProviderStore<Chnl> storeChnl;
	private NamedProviderStore<BinaryChnl> storeBinaryChnl;
	private NamedProviderStore<BinarySgmn> storeBinarySgmn;
	// END: Stores
		
	// START: Single Items
	private RandomNumberGenerator re = new RandomNumberGeneratorMersenneTime();
	private Path modelDir;
	// END: Single Items
	
	private ProviderBridge<StackProvider,Stack,ImageInitParams> stackProviderBridge;

	private ImageInitParams(SharedObjects so, RandomNumberGenerator re, Path modelDir) {
		super();
		this.re = re;
		this.soParams = KeyValueParamsInitParams.create(so);
		this.soFeature = SharedFeaturesInitParams.create(so);
		
		storeStack = so.getOrCreate(Stack.class);
		storeHistogram = so.getOrCreate(Histogram.class);
		storeObjMaskCollection = so.getOrCreate(ObjMaskCollection.class);
		storeChnl = so.getOrCreate(Chnl.class);
		storeBinaryChnl = so.getOrCreate(BinaryChnl.class);
		storeBinarySgmn = so.getOrCreate(BinarySgmn.class);
		this.modelDir = modelDir;
	}
	
	public static ImageInitParams create( SharedObjects so, Path modelDir ) {
		return create(so, new RandomNumberGeneratorMersenneTime(), modelDir );
	}
	
	public static ImageInitParams create( SharedObjects so, RandomNumberGenerator re, Path modelDir ) {
		return new ImageInitParams(so, re, modelDir);
	}
	
	public static ImageInitParams create( LogErrorReporter logErrorReporter, RandomNumberGenerator re, Path modelDir ) {
		SharedObjects so = new SharedObjects(logErrorReporter);
		return ImageInitParams.create(so,re, modelDir);
	}
	
	public NamedProviderStore<Stack> getStackCollection() {
		return storeStack;
	}
	
	public NamedProviderStore<Histogram> getHistogramCollection() {
		return storeHistogram;
	}
	
	public NamedProviderStore<ObjMaskCollection> getObjMaskCollection() {
		return storeObjMaskCollection;
	}
	
	public NamedProviderStore<Chnl> getChnlCollection() {
		return storeChnl;
	}
	
	public NamedProviderStore<BinaryChnl> getBinaryImageCollection() {
		return storeBinaryChnl;
	}
	
	public NamedProviderStore<BinarySgmn> getBinarySgmnSet() {
		return storeBinarySgmn;
	}

	public KeyValueParamsInitParams getParams() {
		return soParams;
	}

	public SharedFeaturesInitParams getFeature() {
		return soFeature;
	}
	
	public void populate( PropertyInitializer<?> pi, Define namedDefinitions, LogErrorReporter logErrorReporter ) throws OperationFailedException {
		
		soFeature.addAll(
			namedDefinitions.getList(FeatureListProvider.class),
			logErrorReporter
		);
		
		stackProviderBridge = new ProviderBridge<StackProvider,Stack,ImageInitParams>(pi, logErrorReporter );
		BeanStoreAdder.addPreserveName( namedDefinitions.getList( BinarySgmn.class ), getBinarySgmnSet(), new IdentityBridgeInit<BinarySgmn,ImageInitParams>(pi,logErrorReporter) );
		BeanStoreAdder.addPreserveName( namedDefinitions.getList( StackProvider.class ), getStackCollection(), stackProviderBridge );
		BeanStoreAdder.addPreserveName( namedDefinitions.getList( BinaryImgChnlProvider.class ), getBinaryImageCollection(), new ProviderBridge<BinaryImgChnlProvider,BinaryChnl,ImageInitParams>(pi, logErrorReporter ) );
		BeanStoreAdder.addPreserveName( namedDefinitions.getList( ChnlProvider.class ), getChnlCollection(), new ProviderBridge<ChnlProvider,Chnl,ImageInitParams>(pi, logErrorReporter ) );
		BeanStoreAdder.addPreserveName( namedDefinitions.getList( ObjMaskProvider.class ), getObjMaskCollection(), new ProviderBridge<ObjMaskProvider,ObjMaskCollection,ImageInitParams>(pi, logErrorReporter ) );
		BeanStoreAdder.addPreserveName( namedDefinitions.getList( HistogramProvider.class ), getHistogramCollection(), new ProviderBridge<HistogramProvider,Histogram,ImageInitParams>(pi, logErrorReporter ) );
	}
	
	
	public void addToStackCollection(String identifier, Stack inputImage) throws OperationFailedException {
		getStackCollection().add(identifier, new IdentityOperation<>(inputImage));
	}
	
	public void addToStackCollection(String identifier, StackProvider stackProvider ) throws OperationFailedException {
		BeanStoreAdder.add(identifier, stackProvider, getStackCollection(), stackProviderBridge);
	}
	
	public void copyStackCollectionFrom( INamedProvider<Stack> stackCollectionSource ) throws OperationFailedException {

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
	
	public void copyObjMaskCollectionFrom( INamedProvider<ObjMaskCollection> collectionSource ) throws OperationFailedException {

		try {
			for (String id : collectionSource.keys()) {
				ObjMaskCollection objs = collectionSource.getException(id);
				if (objs!=null) {
					addToObjMaskCollection(id, new IdentityOperation<ObjMaskCollection>(objs) );
				}
			}
		} catch (NamedProviderGetException e) {
			throw new OperationFailedException(e.summarize());
		}
	}
	
	public void addToObjMaskCollection(String identifier, final Operation<ObjMaskCollection> opObjMaskCollection) throws OperationFailedException {
		getObjMaskCollection().add(identifier, opObjMaskCollection);
	}
	
	public void addToKeyValueParamsCollection( String identifier, KeyValueParams params ) throws OperationFailedException {
		getParams().getNamedKeyValueParamsCollection().add( identifier, new IdentityOperation<>(params));
	}

	public RandomNumberGenerator getRandomNumberGenerator() {
		return re;
	}

	public Path getModelDir() {
		return modelDir;
	}
}
