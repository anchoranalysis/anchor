/*-
 * #%L
 * anchor-feature-session
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.feature.session;

import org.anchoranalysis.bean.exception.BeanMisconfiguredException;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.calculate.FeatureInitialization;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.calculate.cache.FeatureCalculationInput;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.results.ResultsVector;
import org.anchoranalysis.feature.session.calculator.multi.FeatureCalculatorMulti;
import org.anchoranalysis.feature.session.replace.BoundReplaceStrategy;
import org.anchoranalysis.feature.session.replace.ReplaceStrategy;
import org.anchoranalysis.feature.session.replace.ReuseSingletonStrategy;
import org.anchoranalysis.feature.shared.SharedFeatures;

/**
 * Calculates features with successively different parameters, taking care of caching etc.
 * appropriately.
 *
 * <p>All feature use the same initialization, but successively different {#FeatureCalculation}
 * sequentially.
 *
 * <p>Caching is applied only within each call to {{@link #calculate(FeatureInput)} but among
 * successive calls.
 *
 * @author Owen Feehan
 * @param <T> input-type for feature
 */
public class SequentialSession<T extends FeatureInput> implements FeatureCalculatorMulti<T> {

    private static final String ERROR_NOT_STARTED =
            "Session has not been started yet. Call start().";

    private FeatureList<T> listFeatures;

    private boolean isStarted = false;

    // Should feature calculation errors be printed to the console?
    private boolean reportErrors = false;

    private ReplaceStrategy<T> replaceSession;

    private BoundReplaceStrategy<T, ? extends ReplaceStrategy<T>> replacePolicyFactory;

    /**
     * Constructor of a session
     *
     * @param feature the feature that will be calculated in the session
     */
    SequentialSession(Feature<T> feature) {
        this(FeatureListFactory.from(feature));
    }

    /**
     * Constructor of a session
     *
     * @param features the features that will be calculated in this session
     */
    SequentialSession(Iterable<Feature<T>> features) {
        this(features, new BoundReplaceStrategy<>(ReuseSingletonStrategy::new));
    }

    /**
     * Constructor of a session
     *
     * @param features the features that will be calculated in this session
     */
    SequentialSession(
            Iterable<Feature<T>> features,
            BoundReplaceStrategy<T, ? extends ReplaceStrategy<T>> replacePolicyFactory) {
        this.replacePolicyFactory = replacePolicyFactory;
        this.listFeatures = FeatureListFactory.fromIterable(features);
    }

    /**
     * Starts the session
     *
     * @param initialization The parameters used to initialise the feature
     * @param logger Logger
     * @param sharedFeatures A list of features that are shared between the features we are
     *     calculating (and thus also init-ed)
     * @throws InitializeException
     */
    public void start(
            FeatureInitialization initialization, SharedFeatures sharedFeatures, Logger logger)
            throws InitializeException {

        if (isStarted) {
            throw new InitializeException("Session has already been started.");
        }

        checkNoIntersectionWithSharedFeatures(sharedFeatures);
        setupCacheAndInit(initialization, sharedFeatures, logger);

        isStarted = true;
    }

    @Override
    public ResultsVector calculate(T input) throws NamedFeatureCalculateException {
        checkIsStarted();
        return calculateCommonExceptionAsVector(input);
    }

    @Override
    public ResultsVector calculate(T input, FeatureList<T> featuresSubset)
            throws NamedFeatureCalculateException {
        checkIsStarted();

        try {
            return replaceSession.createOrReuse(input).calculate(featuresSubset);
        } catch (Exception e) {
            throw new NamedFeatureCalculateException(e);
        }
    }

    /**
     * Calculates the next-object in our sequential series, reporting any exceptions into a reporter
     * log
     *
     * @param input
     * @return
     */
    public ResultsVector calculateSuppressErrors(T input, ErrorReporter errorReporter) {

        ResultsVector results = new ResultsVector(listFeatures.size());

        if (!isStarted) {
            errorReporter.recordError(SequentialSession.class, ERROR_NOT_STARTED);
            results.setErrorAll(new OperationFailedException(ERROR_NOT_STARTED));
        } else {
            calculateCommonSuppressErrors(results, input, errorReporter);
        }

        return results;
    }

    public boolean hasSingleFeature() {
        return listFeatures.size() == 1;
    }

    @Override
    public int sizeFeatures() {
        return listFeatures.size();
    }

    private void calculateCommonSuppressErrors(
            ResultsVector results, T input, ErrorReporter errorReporter) {

        // Create cacheable inputs, and record any errors for all features
        FeatureCalculationInput<T> sessionInput;
        try {
            sessionInput = replaceSession.createOrReuse(input);
        } catch (Exception e) {
            // Return all features as errored
            if (reportErrors) {
                errorReporter.recordError(SequentialSession.class, e);
            }
            for (int i = 0; i < listFeatures.size(); i++) {
                results.setError(i, e);
            }
            return;
        }

        // Perform individual calculations on each feature
        for (int i = 0; i < listFeatures.size(); i++) {
            Feature<T> f = listFeatures.get(i);

            try {
                results.set(i, sessionInput.calculate(f));

            } catch (Exception e) {
                if (reportErrors) {
                    errorReporter.recordError(SequentialSession.class, e);
                }
                results.setError(i, e);
            }
        }
    }

    private ResultsVector calculateCommonExceptionAsVector(T input)
            throws NamedFeatureCalculateException {

        FeatureCalculationInput<T> sessionInput;
        try {
            sessionInput = replaceSession.createOrReuse(input);
        } catch (CreateException e) {
            throw new NamedFeatureCalculateException(e);
        }

        ResultsVector res = new ResultsVector(listFeatures.size());
        for (int i = 0; i < listFeatures.size(); i++) {
            Feature<T> feature = listFeatures.get(i);

            try {
                double val = sessionInput.calculate(feature);
                res.set(i, val);
            } catch (Exception e) {
                throw new NamedFeatureCalculateException(feature.getFriendlyName(), e.getMessage());
            }
        }

        return res;
    }

    /**
     * Checks that there's no common features in the featureList and the shared-features as this can
     * create complications with initialisation of caches (recursive initializations)
     *
     * @param sharedFeatures
     * @throws InitializeException
     */
    private void checkNoIntersectionWithSharedFeatures(SharedFeatures sharedFeatures)
            throws InitializeException {
        assert (listFeatures != null);
        try {
            for (Feature<T> f : listFeatures) {

                FeatureList<FeatureInput> allDependents = f.createListChildFeatures();

                for (Feature<FeatureInput> dep : allDependents) {

                    if (sharedFeatures.contains(dep)) {
                        throw new InitializeException(
                                String.format(
                                        "Feature '%s' is found in both the session and in the SharedFeatures",
                                        dep.getFriendlyName()));
                    }
                }
            }

        } catch (BeanMisconfiguredException e) {
            throw new InitializeException(e);
        }
    }

    private void setupCacheAndInit(
            FeatureInitialization initialization, SharedFeatures sharedFeatures, Logger logger)
            throws InitializeException {
        assert (initialization != null);
        FeatureInitialization initializationDup = initialization.duplicateShallow();
        listFeatures.initializeRecursive(initializationDup, logger);

        replaceSession =
                replacePolicyFactory.bind(listFeatures, initializationDup, sharedFeatures, logger);
    }

    private void checkIsStarted() throws NamedFeatureCalculateException {
        if (!isStarted) {
            throw new NamedFeatureCalculateException(ERROR_NOT_STARTED);
        }
    }
}
