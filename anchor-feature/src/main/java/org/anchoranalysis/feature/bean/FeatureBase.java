package org.anchoranalysis.feature.bean;

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

import org.anchoranalysis.bean.init.InitializableBean;
import org.anchoranalysis.bean.init.property.PropertyDefiner;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Base-class for beans that use a {@link FeatureInput} and are initialized by {@link FeatureInitParams}.
 * 
 * @author Owen Feehan
 *
 * @param <T> feature-input type
 */
public abstract class FeatureBase<T extends FeatureInput> extends InitializableBean<Feature<T>,FeatureInitParams> {

	private FeatureDefiner<T> featureDefiner = new FeatureDefiner<>();
	
	protected FeatureBase() {
		super( new PropertyInitializer<FeatureInitParams>(FeatureInitParams.class) );
	}
	
	protected FeatureBase( PropertyInitializer<FeatureInitParams> propertyInitializer) {
		super(propertyInitializer);
	}
	
	@Override
	public PropertyDefiner getPropertyDefiner() {
		return featureDefiner;
	}
	
	/**
	 * The class corresponding to feature input-type (i.e. the {@code T} template parameter).
	 * 
	 * @return
	 */
	public abstract Class<? extends FeatureInput> inputType();
}
