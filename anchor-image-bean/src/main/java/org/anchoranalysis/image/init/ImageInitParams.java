package org.anchoranalysis.image.init;

import java.nio.file.Path;

import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.init.params.BeanInitParams;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.bean.store.BeanStoreAdder;
import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.IdentityOperation;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.image.bean.provider.BinaryChnlProvider;
import org.anchoranalysis.image.bean.provider.ChnlProvider;
import org.anchoranalysis.image.bean.provider.HistogramProvider;
import org.anchoranalysis.image.bean.provider.ObjMaskProvider;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.bean.sgmn.binary.BinarySgmn;
import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
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
	private Path modelDir;
	// END: Single Items
	
	private IObjectBridge<StackProvider,Stack,OperationFailedException> stackProviderBridge;

	public ImageInitParams(SharedObjects so, Path modelDir) {
		super();
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
	
	public void populate( PropertyInitializer<?> pi, Define define, LogErrorReporter logger ) throws OperationFailedException {
		
		soFeature.populate(
			define.getList(FeatureListProvider.class),
			logger
		);
		
		PopulateStoreFromDefine<ImageInitParams> populate = new PopulateStoreFromDefine<>(define, pi, logger);
		
		populate.copyInit(BinarySgmn.class, getBinarySgmnSet());
		populate.copyProvider(BinaryChnlProvider.class, getBinaryImageCollection());
		populate.copyProvider(ChnlProvider.class, getChnlCollection());
		populate.copyProvider(ObjMaskProvider.class, getObjMaskCollection());
		populate.copyProvider(HistogramProvider.class, getHistogramCollection());
		
		stackProviderBridge = populate.copyProvider(StackProvider.class, getStackCollection());
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
					addToObjMaskCollection(id, new IdentityOperation<>(objs) );
				}
			}
		} catch (NamedProviderGetException e) {
			throw new OperationFailedException(e.summarize());
		}
	}
	
	public void addToObjMaskCollection(String identifier, Operation<ObjMaskCollection,OperationFailedException> opObjMaskCollection) throws OperationFailedException {
		getObjMaskCollection().add(identifier, opObjMaskCollection);
	}
	
	public void addToKeyValueParamsCollection( String identifier, KeyValueParams params ) throws OperationFailedException {
		getParams().getNamedKeyValueParamsCollection().add(
			identifier,
			new IdentityOperation<>(params)
		);
	}

	public Path getModelDir() {
		return modelDir;
	}
}
