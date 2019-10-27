package ch.ethz.biol.cell.mpp.nrg;

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
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.session.SequentialSessionSingleFeature;
import org.anchoranalysis.feature.shared.SharedFeatureSet;
import org.anchoranalysis.image.feature.stack.FeatureStackParams;

import ch.ethz.biol.cell.mpp.nrg.nrgscheme.NRGScheme;


/**
 * Creates KeyValueParams for a particular NRGStack that is associated with a NRGScheme
 * 
 * @author FEEHANO
 *
 */
public class KeyValueParamsForImageCreator {

	private NRGScheme nrgScheme;
	private SharedFeatureSet sharedFeatures;
	private LogErrorReporter logger;
	
	public KeyValueParamsForImageCreator(NRGScheme nrgScheme, SharedFeatureSet sharedFeatures,
			LogErrorReporter logger) {
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
		
		FeatureStackParams params = new FeatureStackParams(nrgStack);
		
		FeatureInitParams paramsInit = new FeatureInitParams(kvp);
		paramsInit.setNrgStack(nrgStack);
				
		for( NamedBean<Feature> ni : nrgScheme.getListImageFeatures() ) {
			
			kvp.putIfEmpty(
				ni.getName(),
				calcImageFeature(ni.getItem(), paramsInit, params)
			);
		}
	}
	
	private double calcImageFeature( Feature feature, FeatureInitParams paramsInit, FeatureStackParams params) throws OperationFailedException {

		SequentialSessionSingleFeature session = new SequentialSessionSingleFeature( feature );
		try {
			session.start(paramsInit, sharedFeatures, logger );
		} catch (InitException e) {
			throw new OperationFailedException(e);
		}
	
		try {
			return session.calc( params );
					
		} catch (FeatureCalcException e) {
			throw new OperationFailedException(e);
		}		
	}
}
