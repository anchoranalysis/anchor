package org.anchoranalysis.image.feature.objmask.pair;

import org.anchoranalysis.feature.calc.params.FeatureInputNRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

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



import org.anchoranalysis.image.feature.objmask.FeatureInputSingleObj;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ops.ObjMaskMerger;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class FeatureInputPairObjs extends FeatureInputNRGStack {

	private ObjMask objMask1;
	private ObjMask objMask2;
	
	private ObjMask omMerged = null;
	
	// We cache the parameters
	private FeatureInputSingleObj params1 = null;
	
	// We cache the parameters
	private FeatureInputSingleObj params2 = null;
	
	public FeatureInputPairObjs() {
	}
	
	public FeatureInputPairObjs(ObjMask objMask1, ObjMask objMask2) {
		super();
		this.objMask1 = objMask1;
		this.objMask2 = objMask2;
	}
	
	public FeatureInputPairObjs(ObjMask objMask1, ObjMask objMask2, NRGStackWithParams nrgStack) {
		super(nrgStack);
		this.objMask1 = objMask1;
		this.objMask2 = objMask2;
	}
		
	protected FeatureInputPairObjs( FeatureInputPairObjs src ) {
		super();
		this.objMask1 = src.getObjMask1();
		this.objMask2 = src.getObjMask2();
		this.setNrgStack( src.getNrgStack() );
	}

	public ObjMask getObjMask1() {
		return objMask1;
	}

	public ObjMask getObjMask2() {
		return objMask2;
	}
	
	// Resets merged
	public void setObjMask1(ObjMask objMask1) {
		this.objMask1 = objMask1;
		this.omMerged = null;
	}

	// Resets merged
	public void setObjMask2(ObjMask objMask2) {
		this.objMask2 = objMask2;
		this.omMerged = null;
	}
	
	public FeatureInputSingleObj params1() {
		if (params1==null) {
			params1 = new FeatureInputSingleObj(objMask1);
			params1.setNrgStack( getNrgStack() );
		}
		return params1;
	}
	
	public FeatureInputSingleObj params2() {
		if (params2==null) {
			params2 = new FeatureInputSingleObj(objMask2);
			params2.setNrgStack( getNrgStack() );
		}
		return params2;
	}
	
	/**
	 * Returns a merged version of the two-objects available (or NULL if not available) 
	 * 
	 * @return the merged object-mask
	 */
	public ObjMask getObjMaskMerged() {
		if (omMerged==null) {
			omMerged = ObjMaskMerger.merge(objMask1, objMask2);
		}
		return omMerged;
	}

	@Override
	public FeatureInputPairObjs createInverse() {
		FeatureInputPairObjs out = new FeatureInputPairObjs();
		out.setObjMask1(objMask2);
		out.setObjMask2(objMask1);
		out.setObjMaskMerged(getObjMaskMerged());
		out.setNrgStack( getNrgStack() );
		return out;
	}

	@Override
	public String toString() {
		return String.format(
			"%s vs %s",
			objMask1.centerOfGravity(),
			objMask2.centerOfGravity()
		);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!super.equals(obj)) { return false; }
		
		if (!(obj instanceof FeatureInputPairObjs)) { return false; }
		
		FeatureInputPairObjs objCast = (FeatureInputPairObjs) obj;
		
		if (!objMask1.equals(objCast.objMask1)) {
			return false;
		}
		
		if (!objMask2.equals(objCast.objMask2)) {
			return false;
		}
		
		if (omMerged!=null) {
			if (!omMerged.equals(objCast.getObjMaskMerged())) {
				return false;
			}
		}
		
		return true;

	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.appendSuper( super.hashCode() )
				.append(objMask1)
				.append(objMask2)
				.toHashCode();
	}

	public void setObjMaskMerged(ObjMask omMerged) {
		this.omMerged = omMerged;
	}
	
	
}
