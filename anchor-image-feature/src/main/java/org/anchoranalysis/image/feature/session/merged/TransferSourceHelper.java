package org.anchoranalysis.image.feature.session.merged;

/*-
 * #%L
 * anchor-image-feature
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.session.strategy.child.CacheTransferSource;
import org.anchoranalysis.feature.session.strategy.child.CacheTransferSourceCollection;
import org.anchoranalysis.feature.session.strategy.replace.CacheAndReuseStrategy;
import org.anchoranalysis.feature.session.strategy.replace.bind.BoundReplaceStrategy;
import org.anchoranalysis.image.feature.bean.object.pair.FeatureDeriveFromPair;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class TransferSourceHelper {
	
	public static CacheTransferSourceCollection createTransferSource(
		BoundReplaceStrategy<FeatureInputSingleObject,CacheAndReuseStrategy<FeatureInputSingleObject>> replaceStrategyFirstAndSecond,
		BoundReplaceStrategy<FeatureInputSingleObject,CacheAndReuseStrategy<FeatureInputSingleObject>> replaceStrategyMerged		
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
	
	private static CacheTransferSource<FeatureInputSingleObject> sourceFromExistingCache(
		BoundReplaceStrategy<FeatureInputSingleObject,CacheAndReuseStrategy<FeatureInputSingleObject>> replaceStrategy,			
		List<ChildCacheName> cacheNames
	) {
		return new CacheTransferSource<>(
			() -> replaceStrategy.getStrategy().map(CacheAndReuseStrategy::getCache),
			new HashSet<>(cacheNames)
		);
	}
}
