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


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptor;
import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptorUtilities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PROTECTED)
public abstract class FeatureListElem<T extends FeatureInput> extends Feature<T> {

	// START BEAN PARAMETERS
	@BeanField @Getter
	private List<Feature<T>> list = new ArrayList<>();
	// END BEAN PARAMETERS
	
	/**
	 * Constructor
	 * 
	 * @param featureList feature-list
	 */
	protected FeatureListElem(FeatureList<T> featureList) {
		this.list = featureList.asList();
	}
	
	/**
	 * A string description of all the items of the list concatenated together with a character in between
	 * 
	 * @param list
	 * @param operatorDscr
	 * @return
	 */
	protected String descriptionForList(String operatorDscr) {
		List<String> featureDscrs = list.stream()
				.map( Feature::getDscrLong)
				.collect( Collectors.toList() );
		
		return String.join(operatorDscr, featureDscrs);
	}
	
	public void setList(List<Feature<T>> list) {
		this.list = list;
	}
	
	public void setList(FeatureList<T> list) {
		this.list = list.asList();
	}

	@Override
	public FeatureInputDescriptor inputDescriptor() {
		return FeatureInputDescriptorUtilities.paramTypeForList(list);
	}
}
