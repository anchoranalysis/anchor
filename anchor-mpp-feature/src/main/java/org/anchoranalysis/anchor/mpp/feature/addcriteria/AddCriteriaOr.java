package org.anchoranalysis.anchor.mpp.feature.addcriteria;

/*
 * #%L
 * anchor-mpp
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
import java.util.Optional;

import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.image.extent.ImageDimensions;

public class AddCriteriaOr extends AddCriteriaPair {

	// START BEAN PROPERTIES
	@BeanField
	private List<AddCriteriaPair> list = new ArrayList<>();
	// END BEAN PROPERTIES

	@Override
	public boolean includeMarks(PxlMarkMemo mark1, PxlMarkMemo mark2, ImageDimensions dim, Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session, boolean do3D) throws IncludeMarksFailureException {
	
		for( int i=0; i<list.size(); i++) {
	
			AddCriteriaPair acp = list.get(i);
			
			if (acp.includeMarks(mark1, mark2, dim, session, do3D )) {
				return true;
			}
		}
	
		return false;
	}

	public List<AddCriteriaPair> getList() {
		return list;
	}

	public void setList(List<AddCriteriaPair> list) {
		this.list = list;
	}

	@Override
	public boolean paramsEquals(Object other) {

		if (!(other instanceof AddCriteriaOr)) {
			return false;
		}
		
		AddCriteriaOr obj = (AddCriteriaOr) other;
		
		if (list.size()!=obj.list.size()) {
			return false;
		}
		
		for( int i=0; i<list.size(); i++ ) {
			if (!list.get(i).paramsEquals(obj.list.get(i))) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public Optional<FeatureList<FeatureInputPairMemo>> orderedListOfFeatures() throws CreateException {
		return OrderedFeatureListCombine.combine(list);
	}
	
	
}
