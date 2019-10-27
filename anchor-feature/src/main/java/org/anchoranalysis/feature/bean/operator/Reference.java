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
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.CacheSession;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.calc.params.FeatureCalcParamsDescriptor;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.params.FeatureParamsDescriptor;

public class Reference extends Feature {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2384735833480137231L;
	
	// START BEAN
	@BeanField
	private String id;
	// END BEAN
	
	private String rslvdID;
	
	public Reference() {
		super();
	}
	
	public Reference( String id ) {
		super();
		this.id = id;
	}

	@Override
	public void beforeCalc(FeatureInitParams params,
			CacheSession cache)
			throws InitException {
		super.beforeCalc(params, cache);
		
		// We resolve the ID before its passed to calcFeatureByID
		this.rslvdID = cache.resolveFeatureID(id);
	}
	
	
	@Override
	public double calc(FeatureCalcParams params) throws FeatureCalcException {
		return getCacheSession().calcFeatureByID(rslvdID, params);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public void addAdditionallyUsedFeatures(FeatureList out) {
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
	public FeatureParamsDescriptor paramType()
			throws FeatureCalcException {

		return FeatureCalcParamsDescriptor.instance;
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
