package org.anchoranalysis.image.feature.object.calculation;

/*
 * #%L
 * anchor-image-feature
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


import org.anchoranalysis.feature.cache.calculation.FeatureCalculation;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.object.ObjectMask;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class CalculateNumVoxels extends FeatureCalculation<Double,FeatureInputSingleObject> {

	private boolean mip=false;
	
	public CalculateNumVoxels(boolean mip) {
		super();
		this.mip = mip;
	}
	
	public static double calc(ObjectMask om, boolean mip) {
		if (mip==true) {
			om = om.maxIntensityProjection();
		}
		return om.numVoxelsOn();		
	}
	

	// Public, as it's needed by Mockito in test verifications
	@Override
	public Double execute(FeatureInputSingleObject params) {
		return calc( params.getObjectMask(), mip );
	}
	
	@Override
	public boolean equals(final Object obj){
		if(obj instanceof CalculateNumVoxels){
			final CalculateNumVoxels other = (CalculateNumVoxels) obj;
			return new EqualsBuilder()
	            .append(mip, other.mip)
	            .isEquals();
	    } else {
	        return false;
	    }
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(mip).toHashCode();
	}

}