package org.anchoranalysis.feature.bean.operator;

/*
 * #%L
 * anchor-feature
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


import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureInput;
import org.anchoranalysis.feature.params.FeatureInputDescriptor;

/**
 * A feature that contains another feature as a property (the single element)
 * 
 * @author owen
 *
 * @param <T> input-type used for calculating feature
 * @param <S> input-type used for the "item" that is the single element
 */
public abstract class FeatureSingleElem<T extends FeatureInput, S extends FeatureInput> extends Feature<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PARAMETERS
	@BeanField
	private Feature<S> item = null;
	// END BEAN PARAMETERS
	
	public FeatureSingleElem() {
		
	}

	public FeatureSingleElem( Feature<S> feature  ) {
		this.item = feature;
	}
	
	public Feature<S> getItem() {
		return item;
	}

	public void setItem(Feature<S> item) {
		this.item = item;
	}
	
	@Override
	public FeatureInputDescriptor paramType()
			throws FeatureCalcException {
		return item.paramType();
	}
}
