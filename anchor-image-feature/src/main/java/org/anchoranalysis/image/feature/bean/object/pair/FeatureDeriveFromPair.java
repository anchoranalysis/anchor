package org.anchoranalysis.image.feature.bean.object.pair;

/*-
 * #%L
 * anchor-plugin-image-feature
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

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.image.feature.object.calculation.CalculateInputFromPair;
import org.anchoranalysis.image.feature.object.calculation.CalculateInputFromPair.Extract;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;

/**
 * Base class for evaluating {@link FeaturePairObjects} in terms of another feature that operations on elements (first, second, merged etc.)
 * 
 * @author Owen Feehan
 *
 */
public abstract class FeatureDeriveFromPair extends FeaturePairObjects {

	// START BEAN PROPERTIES
	@BeanField
	private Feature<FeatureInputSingleObject> item;
	// END BEAN PROPERTIES
	
	protected FeatureDeriveFromPair() {
		// NOTHING TO DO
	}
	
	protected FeatureDeriveFromPair(Feature<FeatureInputSingleObject> item) {
		this.item = item;
	}
	
	public static final ChildCacheName CACHE_NAME_FIRST = new ChildCacheName(FeatureDeriveFromPair.class, "first");
	public static final ChildCacheName CACHE_NAME_SECOND = new ChildCacheName(FeatureDeriveFromPair.class, "second");
	public static final ChildCacheName CACHE_NAME_MERGED = new ChildCacheName(FeatureDeriveFromPair.class, "merged");
	
	protected double valueFromFirst( SessionInput<FeatureInputPairObjects> input ) throws FeatureCalcException {
		return featureValFrom(
			input,
			Extract.FIRST,
			CACHE_NAME_FIRST
		);
	}
	
	protected double valueFromSecond( SessionInput<FeatureInputPairObjects> input ) throws FeatureCalcException {
		return featureValFrom(
			input,
			Extract.SECOND,
			CACHE_NAME_SECOND
		);
	}
	
	protected double valueFromMerged( SessionInput<FeatureInputPairObjects> input ) throws FeatureCalcException {
		return featureValFrom(
			input,
			Extract.MERGED,
			CACHE_NAME_MERGED
		);
	}
	
	private double featureValFrom( SessionInput<FeatureInputPairObjects> input, Extract extract, ChildCacheName cacheName ) throws FeatureCalcException {
	
		return input.forChild().calc(
			item,
			new CalculateInputFromPair(extract),
			cacheName
		);
	}

	public Feature<FeatureInputSingleObject> getItem() {
		return item;
	}

	public void setItem(Feature<FeatureInputSingleObject> item) {
		this.item = item;
	}
	

}
