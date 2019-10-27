package org.anchoranalysis.image.feature.flexi;

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

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.image.init.ImageInitParams;

public abstract class FeatureSessionFlexiFeatureTable {
	
	/**
	 * Initializes a feature store that has the same structure as that previously created by createFeatures() from the same object
	 * @param soImage
	 * @param soFeature TODO
	 * @param nrgStack TODO
	 * @param logErrorReporter TODO
	 * @param inputFeatureSize TODO
	 * @param features
	 */
	public abstract void start( ImageInitParams soImage, SharedFeaturesInitParams soFeature, NRGStackWithParams nrgStack, LogErrorReporter logErrorReporter ) throws InitException;

	public abstract ResultsVector calcMaybeSuppressErrors( FeatureCalcParams params, ErrorReporter errorReporter ) throws FeatureCalcException;


	
	/**
	 * Makes a copy of the feature-store for a new thread. Deep-copies the features. Shallow-copies everything else.
	 * 
	 * @return
	 */
	public abstract FeatureSessionFlexiFeatureTable duplicateForNewThread();
	
	public abstract FeatureNameList createFeatureNames();
	
	/**
	 * Number of features returned in the ResultsVector from calc()
	 */
	public abstract int size();
}
