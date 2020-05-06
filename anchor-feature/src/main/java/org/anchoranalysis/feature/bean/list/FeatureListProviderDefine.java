package org.anchoranalysis.feature.bean.list;

import java.util.Arrays;

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


import java.util.List;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.SkipInit;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

public class FeatureListProviderDefine<T extends FeatureInput> extends FeatureListProviderReferencedFeatures<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3698511957662882985L;
	
	// START BEAN PROPERTIES
	@BeanField @SkipInit
	private List<Feature<T>> list;
	// END BEAN PROPERTIES
	
	public FeatureListProviderDefine() {
		// DEFAULT, nothing to do
	}
	
	public FeatureListProviderDefine( Feature<T> feature ) {
		this(
			Arrays.asList(feature)
		);
	}
	
	public FeatureListProviderDefine( List<Feature<T>> list ) {
		this.list = list;
	}
	
	@Override
	public FeatureList<T> create() throws CreateException {
		return new FeatureList<>(list);
	}

	public List<Feature<T>> getList() {
		return list;
	}

	public void setList(List<Feature<T>> list) {
		this.list = list;
	}

}
