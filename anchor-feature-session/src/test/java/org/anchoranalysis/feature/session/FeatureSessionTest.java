/* (C)2020 */
package org.anchoranalysis.feature.session;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.FeatureInputNull;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.test.LoggingFixture;
import org.anchoranalysis.test.feature.ConstantsInListFixture;
import org.junit.Test;

public class FeatureSessionTest {

    @Test
    public void testCalculateSimpleListOfFeatures() throws InitException, FeatureCalcException {

        SequentialSession<FeatureInput> session =
                new SequentialSession<>(ConstantsInListFixture.create());
        session.start(
                new FeatureInitParams(),
                new SharedFeatureMulti(),
                LoggingFixture.suppressedLogErrorReporter());

        ResultsVector rv1 = session.calc(FeatureInputNull.instance());
        ConstantsInListFixture.checkResultVector(rv1);

        ResultsVector rv2 = session.calc(FeatureInputNull.instance());
        ConstantsInListFixture.checkResultVector(rv2);
    }
}
