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

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.FeatureInitialization;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.calculator.multi.FeatureCalculatorMulti;
import org.anchoranalysis.feature.session.calculator.single.FeatureCalculatorSingle;
import org.anchoranalysis.feature.session.calculator.single.FeatureCalculatorSingleFromMulti;
import org.anchoranalysis.feature.session.replace.BoundReplaceStrategy;
import org.anchoranalysis.feature.session.replace.ReplaceStrategy;
import org.anchoranalysis.feature.session.replace.ReuseSingletonStrategy;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;

/**
 * A single-point in the code for creating feature-sessions (a factory).
 *
 * <p>A feature session is a context needed to calculate one or more parameters (inptus to features)
 * on one or more features
 *
 * <p>Within this context, caching of intermediate results and other efficiencies are implemented
 * beneath the hood.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeatureSession {

    /**
     * Starts a feature-session for a single feature
     *
     * @param <T> type of parameters
     * @param feature the feature
     * @param logger a logger
     * @return a calculator that will calculate just this feature for each parameter.
     * @throws InitException
     */
    public static <T extends FeatureInput> FeatureCalculatorSingle<T> with(
            Feature<T> feature, Logger logger) throws InitException {
        return with(feature, new FeatureInitialization(), new SharedFeatureMulti(), logger);
    }

    /**
     * Starts a feature-session for a single feature
     *
     * @param <T> type of parameters
     * @param feature the feature
     * @param sharedFeatures
     * @param logger a logger
     * @return a calculator that will calculate just this feature for each parameter.
     */
    public static <T extends FeatureInput> FeatureCalculatorSingle<T> with(
            Feature<T> feature, SharedFeatureMulti sharedFeatures, Logger logger)
            throws InitException {
        return with(feature, new FeatureInitialization(), sharedFeatures, logger);
    }

    public static <T extends FeatureInput> FeatureCalculatorSingle<T> with(
            Feature<T> feature,
            FeatureInitialization initialization,
            SharedFeatureMulti sharedFeatures,
            Logger logger)
            throws InitException {
        SequentialSession<T> session = new SequentialSession<>(feature);
        startSession(session, initialization, sharedFeatures, logger);
        return new FeatureCalculatorSingleFromMulti<>(session);
    }

    /**
     * Starts a feature-session for a list of features
     *
     * @param <T> type of parameters for all features
     * @param features a list of features accepting uniform type
     * @param logger a logger
     * @return a calculator that will call calculate all the features in the list for each
     *     parameter.
     */
    public static <T extends FeatureInput> FeatureCalculatorMulti<T> with(
            FeatureList<T> features, Logger logger) throws InitException {
        return with(features, new FeatureInitialization(), new SharedFeatureMulti(), logger);
    }

    public static <T extends FeatureInput> FeatureCalculatorMulti<T> with(
            FeatureList<T> features,
            FeatureInitialization initialization,
            SharedFeatureMulti sharedFeatures,
            Logger logger)
            throws InitException {
        return with(
                features,
                initialization,
                Optional.of(sharedFeatures),
                logger,
                new BoundReplaceStrategy<>(ReuseSingletonStrategy::new));
    }

    public static <T extends FeatureInput> FeatureCalculatorMulti<T> with(
            FeatureList<T> features,
            FeatureInitialization initialization,
            Optional<SharedFeatureMulti> sharedFeatures,
            Logger logger,
            BoundReplaceStrategy<T, ? extends ReplaceStrategy<T>> replacePolicyFactory)
            throws InitException {
        SequentialSession<T> session = new SequentialSession<>(features, replacePolicyFactory);
        startSession(
                session, initialization, sharedFeatures.orElse(new SharedFeatureMulti()), logger);
        return session;
    }

    /**
     * Starts a feature-session for a single feature - and performs a calculation
     *
     * @param <T> type of input
     * @param feature the feature
     * @param input for features
     * @param logger a logger
     * @return the calculated result
     * @throws FeatureCalculationException if the feature cannot be initialized or cannot be
     *     calculated
     */
    public static <T extends FeatureInput> double calculateWith(
            Feature<T> feature, T input, Logger logger) throws FeatureCalculationException {
        try {
            FeatureCalculatorSingle<T> calculator = with(feature, logger);
            return calculator.calculate(input);
        } catch (InitException e) {
            throw new FeatureCalculationException(e);
        }
    }

    private static <T extends FeatureInput> void startSession(
            SequentialSession<T> session,
            FeatureInitialization initialization,
            SharedFeatureMulti sharedFeatures,
            Logger logger)
            throws InitException {
        try {
            session.start(initialization, sharedFeatures, logger);
        } catch (InitException e) {
            throw new InitException(
                    "An error occurred starting the feature (sequential) session", e);
        }
    }
}
