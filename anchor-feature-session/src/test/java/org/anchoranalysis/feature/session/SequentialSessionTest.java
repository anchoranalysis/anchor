package org.anchoranalysis.feature.session;

/*-
 * #%L
 * anchor-feature-session
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
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.experiment.log.ConsoleLogReporter;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.NullParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.shared.SharedFeatureSet;
import org.anchoranalysis.test.feature.SimpleFeatureListFixture;
import org.junit.Test;

public class SequentialSessionTest {
	
	@Test
	public void testCalculateSimpleListOfFeatures() throws InitException, FeatureCalcException {
		
		SequentialSession session = new SequentialSession(SimpleFeatureListFixture.create());
		session.start( new FeatureInitParams(), new SharedFeatureSet(), new LogErrorReporter( new ConsoleLogReporter() ) );
		
		ResultsVector rv1 = session.calc( NullParams.instance() );
		SimpleFeatureListFixture.checkResultVector(rv1);
		
		ResultsVector rv2 = session.calc( NullParams.instance() );
		SimpleFeatureListFixture.checkResultVector(rv2);
	}
}
