package org.anchoranalysis.image.feature.session;

import java.util.Optional;

/*-
 * #%L
 * anchor-plugin-mpp-experiment
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

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.list.NamedFeatureStore;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;

public class SingleObjTableSession implements FeatureTableCalculator<FeatureInputSingleObject> {

	private FeatureCalculatorMulti<FeatureInputSingleObject> session;

	private NamedFeatureStore<FeatureInputSingleObject> namedFeatureStore;
	
	public SingleObjTableSession(NamedFeatureStore<FeatureInputSingleObject> namedFeatureStore) {
		this.namedFeatureStore = namedFeatureStore;
	}

	@Override
	public void start(
		ImageInitParams soImage,
		Optional<NRGStackWithParams> nrgStack,
		Logger logger
	) throws InitException {
		
		try {
			session = FeatureSession.with(
				namedFeatureStore.listFeatures(),
				InitParamsHelper.createInitParams(soImage,nrgStack),
				soImage.getFeature().getSharedFeatureSet(),
				logger
			);
		} catch (FeatureCalcException e) {
			throw new InitException(e);
		}
	}
	
	@Override
	public FeatureTableCalculator<FeatureInputSingleObject> duplicateForNewThread() {
		return new SingleObjTableSession(namedFeatureStore.deepCopy());
	}

	@Override
	public ResultsVector calc(FeatureInputSingleObject input) throws FeatureCalcException {
		return session.calc(input);
	}

	@Override
	public ResultsVector calc(FeatureInputSingleObject input, FeatureList<FeatureInputSingleObject> featuresSubset) throws FeatureCalcException {
		return session.calc(input, featuresSubset);
	}

	@Override
	public ResultsVector calcSuppressErrors(FeatureInputSingleObject input, ErrorReporter errorReporter) {
		return session.calcSuppressErrors( input, errorReporter );
	}

	@Override
	public int sizeFeatures() {
		return namedFeatureStore.size();
	}
		
	@Override
	public FeatureNameList createFeatureNames() {
		return namedFeatureStore.createFeatureNames();
	}
}
