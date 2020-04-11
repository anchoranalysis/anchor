package org.anchoranalysis.anchor.mpp.feature.addcriteria;

import org.anchoranalysis.anchor.mpp.feature.nrg.elem.NRGElemPairCalcParams;
import org.anchoranalysis.anchor.mpp.mark.points.MarkPointList;
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


import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.session.SequentialSession;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;

/** If one arbitrarily point overlaps between two MarkPointList then TRUE, otherwise FALSE
 * 
 *  This is useful for mutually exclusive sets of points, where iff one arbitrary point intersects with
 *    another set, then they must be identical.
 *  */
public class ArbitraryPointCommon extends AddCriteriaPair {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7032818241337209355L;
	
	// START BEAN PROPERTIES
	// END BEAN PROPERTIES
	
	@Override
	public boolean includeMarks(PxlMarkMemo mark1, PxlMarkMemo mark2, ImageDim dim, SequentialSession<NRGElemPairCalcParams> session, boolean use3D) throws IncludeMarksFailureException {
			
		BoundingBox bbox1 = mark1.getMark().bboxAllRegions(dim);
		BoundingBox bbox2 = mark2.getMark().bboxAllRegions(dim);
		
		// If their bounding boxes don't intersect, they cannot possibly have an overlapping point
		if (!bbox1.hasIntersection(bbox2)) {
			return false;
		}

		MarkPointList mark1Cast = (MarkPointList) mark1.getMark();
		MarkPointList mark2Cast = (MarkPointList) mark2.getMark();
		
		// Check for intersection of an arbitrary point
		return mark2Cast.getPoints().contains( mark1Cast.getPoints().get(0) );
	}

	@Override
	public boolean paramsEquals(Object other) {
		
		if( (other instanceof ArbitraryPointCommon)) {
			return false;
		}
		
		return true;
	}


	@Override
	public FeatureList<NRGElemPairCalcParams> orderedListOfFeatures() {
		// No features involved
		return null;
	}
}
