/* (C)2020 */
package org.anchoranalysis.image.feature.session.merged;

import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.strategy.replace.CacheAndReuseStrategy;
import org.anchoranalysis.feature.session.strategy.replace.ReplaceStrategy;
import org.anchoranalysis.feature.session.strategy.replace.ReuseSingletonStrategy;
import org.anchoranalysis.feature.session.strategy.replace.bind.BoundReplaceStrategy;
import org.anchoranalysis.image.feature.stack.FeatureInputStack;

/**
 * Strategies for caching used in {@link FeatureCalculatorMergedPairs}
 *
 * @author Owen Feehan
 */
class CachingStrategies {

    private CachingStrategies() {}

    /** Cache and re-use inputs */
    public static <T extends FeatureInput>
            BoundReplaceStrategy<T, CacheAndReuseStrategy<T>> cacheAndReuse() {
        return new BoundReplaceStrategy<>(CacheAndReuseStrategy::new);
    }

    /* Don't cache inputs */
    public static BoundReplaceStrategy<
                    FeatureInputStack, ? extends ReplaceStrategy<FeatureInputStack>>
            noCache() {
        return new BoundReplaceStrategy<>(ReuseSingletonStrategy::new);
    }
}
