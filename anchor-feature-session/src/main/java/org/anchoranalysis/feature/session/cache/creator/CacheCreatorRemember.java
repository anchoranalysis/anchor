package org.anchoranalysis.feature.session.cache.creator;

/*-
 * #%L
 * anchor-feature-session
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

import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.feature.cache.creator.CacheCreator;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCache;

/**
 * Rememembers all caches created by a delegate CacheCreator and provides a means to invalidate them all
 * 
 * @author owen
 *
 */
public class CacheCreatorRemember implements CacheCreator {

	private CacheCreator delegate;

	private List<FeatureSessionCache<?>> list = new ArrayList<>();
	
	public CacheCreatorRemember(CacheCreator delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public <T extends FeatureCalcParams> FeatureSessionCache<T> create(Class<?> paramsType) {

		FeatureSessionCache<T> cache = delegate.create(paramsType);
		list.add(cache);
		return cache;
	}

	/** Invalidates all the rememembered caches */
	public void invalidateAll() {
		System.out.printf("Invalidating all with %d objects%n", list.size());
		list.stream().forEach(
			item -> item.invalidate()
		);
	}
}
