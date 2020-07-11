package org.anchoranalysis.anchor.mpp.feature.nrg.scheme;

import java.util.Optional;

/*-
 * #%L
 * anchor-mpp-feature
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

import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.image.feature.stack.FeatureInputStack;


/**
 * Creates KeyValueParams for a particular NRGStack that is associated with a NRGScheme
 * 
 * @author Owen Feehan
 *
 */
public class KeyValueParamsForImageCreator {

	private NRGScheme nrgScheme;
	private SharedFeatureMulti sharedFeatures;
	private Logger logger;
	
	public KeyValueParamsForImageCreator(NRGScheme nrgScheme, SharedFeatureMulti sharedFeatures,
			Logger logger) {
		super();
		this.nrgScheme = nrgScheme;
		this.sharedFeatures = sharedFeatures;
		this.logger = logger;
	}
	
	public KeyValueParams createParamsForImage( NRGStack nrgStack ) throws FeatureCalcException {
		try {
			KeyValueParams params = nrgScheme.createKeyValueParams();
			addParamsForImage(nrgStack, params );
			return params;
			
		} catch (CreateException | OperationFailedException e) {
			throw new FeatureCalcException(e);
		}
	}
	
	private void addParamsForImage(
			NRGStack nrgStack,
			KeyValueParams kvp
		) throws OperationFailedException {
		
		FeatureInputStack params = new FeatureInputStack(nrgStack);
		
		FeatureInitParams paramsInit = new FeatureInitParams(
			Optional.of(kvp),
			Optional.of(nrgStack),
			Optional.empty()
		);
				
		for( NamedBean<Feature<FeatureInputStack>> ni : nrgScheme.getListImageFeatures() ) {
			
			kvp.putIfEmpty(
				ni.getName(),
				calcImageFeature(ni.getItem(), paramsInit, params)
			);
		}
	}
	
	private double calcImageFeature( Feature<FeatureInputStack> feature, FeatureInitParams paramsInit, FeatureInputStack params) throws OperationFailedException {

		try {
			FeatureCalculatorSingle<FeatureInputStack> session = FeatureSession.with(
				feature,
				paramsInit,
				sharedFeatures,
				logger
			);			
			
			return session.calc( params );
					
		} catch (FeatureCalcException e) {
			throw new OperationFailedException(e);
		}		
	}
}
