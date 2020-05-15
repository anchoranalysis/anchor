package org.anchoranalysis.anchor.mpp.feature.addcriteria;

import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;

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


import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;

public class BBoxIntersection extends AddCriteriaPair {

	// START BEAN PROPERTIES
	@BeanField
	private boolean suppressZ = false;
	// END BEAN PROPERTIES
	
	@Override
	public boolean includeMarks(PxlMarkMemo mark1, PxlMarkMemo mark2, ImageDim dim, FeatureCalculatorMulti<FeatureInputPairMemo> session, boolean use3D) throws IncludeMarksFailureException {
	
		
		BoundingBox bbox1 = mark1.getMark().bboxAllRegions(dim);
		BoundingBox bbox2 = mark2.getMark().bboxAllRegions(dim);
		
		if (suppressZ) {
			bbox1 = new BoundingBox(bbox1);
			bbox2 = new BoundingBox(bbox2);
			
			bbox1.flattenZ();
			bbox2.flattenZ();
		}
		
		return bbox1.hasIntersection(bbox2);
	}

	@Override
	public boolean paramsEquals(Object other) {
		
		if( (other instanceof BBoxIntersection)) {
			return false;
		}
		
		return true;
	}

	public boolean isSuppressZ() {
		return suppressZ;
	}

	public void setSuppressZ(boolean suppressZ) {
		this.suppressZ = suppressZ;
	}

	@Override
	public FeatureList<FeatureInputPairMemo> orderedListOfFeatures() {
		// No features involved
		return null;
	}
}
