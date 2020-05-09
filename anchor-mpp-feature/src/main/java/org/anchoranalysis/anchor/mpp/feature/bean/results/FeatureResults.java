package org.anchoranalysis.anchor.mpp.feature.bean.results;

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

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVectorCollection;
import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptor;
import org.anchoranalysis.feature.resultsvectorcollection.FeatureInputResults;
import org.anchoranalysis.feature.resultsvectorcollection.FeatureInputResultsDescriptor;


/**
 * Features that process {@link ResultsVectorCollection} i.e. the result of the calculation of some other features.
 * 
 * <p>This is useful for applying some aggregate statistics (min, max etc.) to the results of multiple features calculated together.</p>
 * 
 * @author Owen Feehan
 *
 */
public abstract class FeatureResults extends Feature<FeatureInputResults> {

	@Override
	public double calc( SessionInput<FeatureInputResults> input ) throws FeatureCalcException {
		return calc( input.get() );
	}
	
	// Calculates an NRG element for a set of pixels
	public abstract double calc( FeatureInputResults params ) throws FeatureCalcException;
	
	@Override
	public FeatureInputDescriptor paramType()
			throws FeatureCalcException {
		return FeatureInputResultsDescriptor.instance;
	}

}
