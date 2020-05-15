package org.anchoranalysis.image.feature.session.merged;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.session.strategy.child.CacheTransferSource;
import org.anchoranalysis.feature.session.strategy.child.CacheTransferSourceCollection;
import org.anchoranalysis.feature.session.strategy.replace.CacheAndReuseStrategy;
import org.anchoranalysis.feature.session.strategy.replace.bind.BoundReplaceStrategy;
import org.anchoranalysis.image.feature.objmask.FeatureInputSingleObj;
import org.anchoranalysis.image.feature.objmask.pair.FeatureDeriveFromPair;

class TransferSourceHelper {

	private TransferSourceHelper() {}
	
	public static CacheTransferSourceCollection createTransferSource(
		BoundReplaceStrategy<FeatureInputSingleObj,CacheAndReuseStrategy<FeatureInputSingleObj>> replaceStrategyFirstAndSecond,
		BoundReplaceStrategy<FeatureInputSingleObj,CacheAndReuseStrategy<FeatureInputSingleObj>> replaceStrategyMerged		
	) {

		CacheTransferSourceCollection source = new CacheTransferSourceCollection();
		source.add(
			sourceFromExistingCache(
				replaceStrategyFirstAndSecond,
				Arrays.asList(FeatureDeriveFromPair.CACHE_NAME_FIRST, FeatureDeriveFromPair.CACHE_NAME_SECOND)
			)	
		);
		source.add(
			sourceFromExistingCache(
				replaceStrategyMerged,
				Arrays.asList(FeatureDeriveFromPair.CACHE_NAME_MERGED)
			)
		);
		return source;
	}
	
	private static CacheTransferSource<FeatureInputSingleObj> sourceFromExistingCache(
		BoundReplaceStrategy<FeatureInputSingleObj,CacheAndReuseStrategy<FeatureInputSingleObj>> replaceStrategy,			
		List<ChildCacheName> cacheNames
	) {
		return new CacheTransferSource<>(
			() -> replaceStrategy.getStrategy().map( strategy -> 
				strategy.getCache()
			),
			new HashSet<>(cacheNames)
		);
	}
}
