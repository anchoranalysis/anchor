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


import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureInput;
import org.anchoranalysis.feature.calc.params.FeatureInputGenericDescriptor;
import org.anchoranalysis.feature.params.FeatureInputDescriptor;

public class Reference<T extends FeatureInput> extends Feature<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2384735833480137231L;
	
	// START BEAN
	@BeanField
	private String id;
	// END BEAN
	
	public Reference() {
		super();
	}
	
	public Reference( String id ) {
		super();
		this.id = id;
	}
	
	@Override
	public double calc(SessionInput<T> input) throws FeatureCalcException {
		// We resolve the ID before its passed to calcFeatureByID
		String rslvdID = input.resolveFeatureID(id);
		return input.calcFeatureByID(rslvdID, input);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public void addAdditionallyUsedFeatures(FeatureList<FeatureInput> out) {
		super.addAdditionallyUsedFeatures(out);
//		if (feature!=null) {
//			out.add( feature );
//		}
	}

	@Override
	public String getDscrLong() {
		return "_" + id;
	}

	@Override
	public FeatureInputDescriptor paramType()
			throws FeatureCalcException {

		return FeatureInputGenericDescriptor.instance;
//		assert(sharedFeatures!=null);
//		
//		try {
//			feature = sharedFeatures.getException(id);
//		} catch (GetOperationFailedException e) {
//			throw new FeatureCalcException(e);
//		}
//		
//		if (feature==null) {
//			throw new FeatureCalcException( String.format("Matching id '%s' not found in sharedFeatures", id) );
//		}
//		
//		return feature.paramType();
	}




}
