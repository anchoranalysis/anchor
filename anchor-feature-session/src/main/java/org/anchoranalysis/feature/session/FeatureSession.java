/* (C)2020 */
package org.anchoranalysis.feature.session;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingleFromMulti;
import org.anchoranalysis.feature.session.strategy.replace.ReplaceStrategy;
import org.anchoranalysis.feature.session.strategy.replace.ReuseSingletonStrategy;
import org.anchoranalysis.feature.session.strategy.replace.bind.BoundReplaceStrategy;
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
     * @throws FeatureCalcException
     */
    public static <T extends FeatureInput> FeatureCalculatorSingle<T> with(
            Feature<T> feature, Logger logger) throws FeatureCalcException {
        return with(feature, new FeatureInitParams(), new SharedFeatureMulti(), logger);
    }

    /**
     * Starts a feature-session for a single feature
     *
     * @param <T> type of parameters
     * @param feature the feature
     * @param sharedFeatures
     * @param logger a logger
     * @return a calculator that will calculate just this feature for each parameter.
     * @throws FeatureCalcException
     */
    public static <T extends FeatureInput> FeatureCalculatorSingle<T> with(
            Feature<T> feature, SharedFeatureMulti sharedFeatures, Logger logger)
            throws FeatureCalcException {
        return with(feature, new FeatureInitParams(), sharedFeatures, logger);
    }

    public static <T extends FeatureInput> FeatureCalculatorSingle<T> with(
            Feature<T> feature,
            FeatureInitParams initParams,
            SharedFeatureMulti sharedFeatures,
            Logger logger)
            throws FeatureCalcException {
        SequentialSession<T> session = new SequentialSession<>(feature);
        startSession(session, initParams, sharedFeatures, logger);
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
     * @throws FeatureCalcException
     */
    public static <T extends FeatureInput> FeatureCalculatorMulti<T> with(
            FeatureList<T> features, Logger logger) throws FeatureCalcException {
        return with(features, new FeatureInitParams(), new SharedFeatureMulti(), logger);
    }

    public static <T extends FeatureInput> FeatureCalculatorMulti<T> with(
            FeatureList<T> features,
            FeatureInitParams initParams,
            SharedFeatureMulti sharedFeatures,
            Logger logger)
            throws FeatureCalcException {
        return with(
                features,
                initParams,
                Optional.of(sharedFeatures),
                logger,
                new BoundReplaceStrategy<>(ReuseSingletonStrategy::new));
    }

    public static <T extends FeatureInput> FeatureCalculatorMulti<T> with(
            FeatureList<T> features,
            FeatureInitParams initParams,
            Optional<SharedFeatureMulti> sharedFeatures,
            Logger logger,
            BoundReplaceStrategy<T, ? extends ReplaceStrategy<T>> replacePolicyFactory)
            throws FeatureCalcException {
        SequentialSession<T> session = new SequentialSession<>(features, replacePolicyFactory);
        startSession(session, initParams, sharedFeatures.orElse(new SharedFeatureMulti()), logger);
        return session;
    }

    private static <T extends FeatureInput> void startSession(
            SequentialSession<T> session,
            FeatureInitParams initParams,
            SharedFeatureMulti sharedFeatures,
            Logger logger)
            throws FeatureCalcException {
        try {
            session.start(initParams, sharedFeatures, logger);
        } catch (InitException e) {
            throw new FeatureCalcException(
                    "An error occurred starting the feature (sequential) session", e);
        }
    }
}
