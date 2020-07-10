package org.anchoranalysis.image.feature.session.merged;

import org.anchoranalysis.feature.input.FeatureInput;

/*-
 * #%L
 * anchor-plugin-mpp-experiment
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

import org.anchoranalysis.feature.session.strategy.replace.CacheAndReuseStrategy;
import org.anchoranalysis.feature.session.strategy.replace.ReplaceStrategy;
import org.anchoranalysis.feature.session.strategy.replace.ReuseSingletonStrategy;
import org.anchoranalysis.feature.session.strategy.replace.bind.BoundReplaceStrategy;
import org.anchoranalysis.image.feature.stack.FeatureInputStack;

/**
 * Strategies for caching used in {@link FeatureCalculatorMergedPairs}
 * 
 * @author Owen Feehan
 *
 */
class CachingStrategies {

	private CachingStrategies() {}
	
	/** Cache and re-use inputs */
	public static <T extends FeatureInput> BoundReplaceStrategy<T,CacheAndReuseStrategy<T>> cacheAndReuse() {
		return new BoundReplaceStrategy<>(CacheAndReuseStrategy::new);
	}
	
	/* Don't cache inputs */
	public static BoundReplaceStrategy<FeatureInputStack,? extends ReplaceStrategy<FeatureInputStack>> noCache() {
		return new BoundReplaceStrategy<>(ReuseSingletonStrategy::new);
	}
}
