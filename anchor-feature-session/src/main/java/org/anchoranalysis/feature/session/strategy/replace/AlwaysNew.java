package org.anchoranalysis.feature.session.strategy.replace;

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

import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.cache.calculation.CacheCreator;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.SessionInputSequential;
import org.anchoranalysis.feature.session.strategy.child.DefaultFindChildStrategy;
import org.anchoranalysis.feature.session.strategy.child.FindChildStrategy;

/**
 * Always create a new session-input with no reuse or caching
 * 
 * @author Owen Feehan
 *
 * @param <T> feature-input type
 */
public class AlwaysNew<T extends FeatureInput> extends ReplaceStrategy<T> {

	private CacheCreator cacheCreator;
	private FindChildStrategy findChildStrategy;
	
	/**
	 * Constructor with default means of creating a session-input
	 * 
	 * @param createSessionInput
	 */
	public AlwaysNew(CacheCreator cacheCreator) {
		this(
			cacheCreator,
			DefaultFindChildStrategy.instance()		
		);
	}
	
	/**
	 * Constructor with custom means of creating a session-input
	 * 
	 * @param createSessionInput
	 */
	public AlwaysNew(CacheCreator cacheCreator, FindChildStrategy findChildStrategy) {
		this.cacheCreator = cacheCreator;
		this.findChildStrategy = findChildStrategy;
	}
	
	@Override
	public SessionInput<T> createOrReuse(T input) throws FeatureCalcException {
		return new SessionInputSequential<>(
			input,
			cacheCreator,
			findChildStrategy
		);
	}

}
