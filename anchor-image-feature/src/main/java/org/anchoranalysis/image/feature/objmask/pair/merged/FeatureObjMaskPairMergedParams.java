package org.anchoranalysis.image.feature.objmask.pair.merged;

/*-
 * #%L
 * anchor-image-feature
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.image.feature.objmask.pair.FeatureObjMaskPairParams;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ops.ObjMaskMerger;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class FeatureObjMaskPairMergedParams extends FeatureObjMaskPairParams {
	
	private ObjMask merged;

	public FeatureObjMaskPairMergedParams() {
		super();
	}
	
	
	public FeatureObjMaskPairMergedParams(ObjMask objMask1,ObjMask objMask2 ) {
		super(objMask1,objMask2);
		this.merged = ObjMaskMerger.merge(objMask1, objMask2 );
	}
	
	public FeatureObjMaskPairMergedParams(ObjMask objMask1,ObjMask objMask2,ObjMask omMerged) {
		super(objMask1,objMask2);
		this.setObjMaskMerged(omMerged);
	}
	
	private FeatureObjMaskPairMergedParams( FeatureObjMaskPairParams parent, ObjMask omMerged ) {
		super(parent);
		this.setObjMaskMerged(omMerged);
	}
	
	@Override
	public FeatureObjMaskPairMergedParams createInverse() {
		return new FeatureObjMaskPairMergedParams( super.createInverse(), merged );
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!super.equals(obj)) { return false; }
		
		if (!(obj instanceof FeatureObjMaskPairMergedParams)) { return false; }
		
		FeatureObjMaskPairMergedParams objCast = (FeatureObjMaskPairMergedParams) obj;

		if (!merged.equals(objCast.merged)) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.appendSuper( super.hashCode() )
				.append(merged)
				.toHashCode();
	}

}
