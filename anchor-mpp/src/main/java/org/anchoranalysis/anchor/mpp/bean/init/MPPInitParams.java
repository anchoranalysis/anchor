package org.anchoranalysis.anchor.mpp.bean.init;

/*-
 * #%L
 * anchor-mpp
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

import org.anchoranalysis.anchor.mpp.bean.MPPBean;
import org.anchoranalysis.anchor.mpp.bean.bound.MarkBounds;
import org.anchoranalysis.anchor.mpp.bean.cfg.CfgProvider;
import org.anchoranalysis.anchor.mpp.bean.proposer.CfgProposer;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkMergeProposer;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkSplitProposer;
import org.anchoranalysis.anchor.mpp.bean.provider.ProbMapProvider;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.mpp.pair.PairCollection;
import org.anchoranalysis.anchor.mpp.probmap.ProbMap;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.bean.init.params.BeanInitParams;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.image.init.ImageInitParams;
import org.anchoranalysis.image.init.PopulateStoreFromDefine;

// A wrapper around SharedObjects which types certain MPP entities
public class MPPInitParams extends BeanInitParams {

	// START: InitParams
	private ImageInitParams soImage;
	private PointsInitParams soPoints;
	// END: InitParams
	
	// START: Stores
	private NamedProviderStore<Cfg> storeCfg;
	private NamedProviderStore<CfgProposer> storeCfgProposer;
	private NamedProviderStore<MarkBounds> storeMarkBounds;
	private NamedProviderStore<MarkProposer> storeMarkProposer;
	private NamedProviderStore<MarkMergeProposer> storeMarkMergeProposer;
	private NamedProviderStore<MarkSplitProposer> storeMarkSplitProposer;
	private NamedProviderStore<ProbMap> storeProbMap;
	private NamedProviderStore<PairCollection<Pair<Mark>>> storePairCollection;
	// END: Stores

	private MPPInitParams(ImageInitParams soImage, SharedObjects so) {
		super();
		this.soImage = soImage;
		this.soPoints = PointsInitParams.create( soImage, so );
		
		storeCfg = so.getOrCreate(Cfg.class);
		storeCfgProposer = so.getOrCreate(CfgProposer.class);
		storeMarkBounds = so.getOrCreate(MarkBounds.class);
		storeMarkProposer = so.getOrCreate(MarkProposer.class);
		storeMarkMergeProposer = so.getOrCreate(MarkMergeProposer.class);
		storeMarkSplitProposer = so.getOrCreate(MarkSplitProposer.class);
		storeProbMap = so.getOrCreate(ProbMap.class);
		storePairCollection = so.getOrCreate(PairCollection.class);
	}
	
	public static MPPInitParams create( ImageInitParams soImage, SharedObjects so ) {
		return new MPPInitParams( soImage, so);
	}
	
	public static MPPInitParams create( SharedObjects so, Path modelDir ) {
		return create( ImageInitParams.create(so, modelDir), so );
	}
	
	public static MPPInitParams create(
		SharedObjects so,
		Define namedDefinitions,
		GeneralInitParams paramsGeneral
	) throws CreateException {
		ImageInitParams soImage = ImageInitParams.create( so, paramsGeneral.getRe(), paramsGeneral.getModelDir() );
		MPPInitParams soMPP = create( soImage, so);
		if (namedDefinitions!=null) {
			try {
				// Tries to initialize any properties (of type MPPInitParams) found in the NamedDefinitions
				PropertyInitializer<MPPInitParams> pi = MPPBean.initializerForMPPBeans();
				pi.setParam(soMPP);
				soMPP.populate( pi, namedDefinitions, paramsGeneral.getLogErrorReporter() );

			} catch (OperationFailedException e) {
				throw new CreateException(e);
			}
		}
		
		return soMPP;
	}

	public ImageInitParams getImage() {
		return soImage;
	}
	
	public SharedFeaturesInitParams getFeature() {
		return soImage.getFeature();
	}
	
	public PointsInitParams getPoints() {
		return soPoints;
	}
	
	public KeyValueParamsInitParams getParams() {
		return soImage.getParams();
	}
	
	public NamedProviderStore<Cfg> getCfgCollection() {
		return storeCfg;
	}
	
	public NamedProviderStore<CfgProposer> getCfgProposerSet() {
		return storeCfgProposer;
	}
	
	public NamedProviderStore<MarkBounds> getMarkBoundsSet() {
		return storeMarkBounds;
	}
	
	public NamedProviderStore<MarkProposer> getMarkProposerSet() {
		return storeMarkProposer;
	}
	
	public NamedProviderStore<MarkMergeProposer> getMarkMergeProposerSet() {
		return storeMarkMergeProposer;
	}
	
	public NamedProviderStore<MarkSplitProposer> getMarkSplitProposerSet() {
		return storeMarkSplitProposer;
	}
	
	public NamedProviderStore<ProbMap> getProbMapSet() {
		return storeProbMap;
	}
	
	public NamedProviderStore<PairCollection<Pair<Mark>>> getSimplePairCollection() {
		return storePairCollection;
	}
	
	/**
	 * 
	 * Backwards compatible for when we only had a single bounds
	 * 
	 * This is stored in all our named-definition files, from now on as "primary"
	 * 
	 * @return
	 * @throws GetOperationFailedException
	 */
	public MarkBounds getMarkBounds() throws NamedProviderGetException {
		return getMarkBoundsSet().getException("primary");
	}

	public void populate( PropertyInitializer<?> pi, Define define, LogErrorReporter logger ) throws OperationFailedException {
		
		PopulateStoreFromDefine<MPPInitParams> populater = new PopulateStoreFromDefine<>(define, pi, logger);
		populater.copyWithoutInit(MarkBounds.class, getMarkBoundsSet());
		populater.copyInit(MarkProposer.class, getMarkProposerSet());
		populater.copyInit(CfgProposer.class, getCfgProposerSet());
		populater.copyInit(MarkSplitProposer.class, getMarkSplitProposerSet());
		populater.copyInit(MarkMergeProposer.class, getMarkMergeProposerSet());
		populater.copyWithoutInit(PairCollection.class, getSimplePairCollection());
		
		populater.copyProvider(CfgProvider.class, getCfgCollection());
		populater.copyProvider(ProbMapProvider.class, getProbMapSet());
		
		soImage.populate(pi, define, logger);
		soPoints.populate(pi, define, logger );
	}
}
