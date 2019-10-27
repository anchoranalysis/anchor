package org.anchoranalysis.image.feature.objmask;

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

import org.anchoranalysis.image.feature.stack.nrg.FeatureNRGStackParams;
import org.anchoranalysis.image.objmask.ObjMask;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class FeatureObjMaskParams extends FeatureNRGStackParams {

	private ObjMask objMask;
	
	public FeatureObjMaskParams() {
		
	}
	
	public FeatureObjMaskParams(ObjMask objMask) {
		super();
		this.objMask = objMask;
	}

	public ObjMask getObjMask() {
		return objMask;
	}

	public void setObjMask(ObjMask objMask) {
		this.objMask = objMask;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) { return false; }
		if ( this == obj ) { return true; }
		if ( !(obj instanceof FeatureObjMaskParams) ) { return false; }
		
		FeatureObjMaskParams objCast = (FeatureObjMaskParams)obj;
		
		if (!objMask.equals(objCast.objMask)) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.appendSuper( super.hashCode() )
				.append(objMask)
				.toHashCode();
	}
	
	@Override
	public String toString() {
		return objMask.toString();
	}
}
