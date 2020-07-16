/* (C)2020 */
package org.anchoranalysis.feature.session.strategy.replace.bind;

import java.util.Optional;
import java.util.function.Function;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.calculation.CacheCreator;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.cache.creator.CacheCreatorSimple;
import org.anchoranalysis.feature.session.strategy.replace.ReplaceStrategy;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;

/**
 * Attaches a replacement-strategy to a session lazily (i.e. when it is needed)
 *
 * <p>This is because as the relevant parameters are not available when we need to call the
 * constructor</op>.
 */
public class BoundReplaceStrategy<T extends FeatureInput, S extends ReplaceStrategy<T>> {

    private Optional<S> strategy = Optional.empty();

    private Function<CacheCreator, S> funcCreateStrategy;

    public BoundReplaceStrategy(Function<CacheCreator, S> funcCreateStrategy) {
        super();
        this.funcCreateStrategy = funcCreateStrategy;
    }

    public ReplaceStrategy<T> bind(
            FeatureList<T> featureList,
            FeatureInitParams featureInitParams,
            SharedFeatureMulti sharedFeatures,
            Logger logger) {
        CacheCreator cacheCreator =
                new CacheCreatorSimple(featureList, sharedFeatures, featureInitParams, logger);
        if (!strategy.isPresent()) {
            strategy = Optional.of(funcCreateStrategy.apply(cacheCreator));
        }
        return strategy.get();
    }

    public Optional<S> getStrategy() {
        return strategy;
    }
}
