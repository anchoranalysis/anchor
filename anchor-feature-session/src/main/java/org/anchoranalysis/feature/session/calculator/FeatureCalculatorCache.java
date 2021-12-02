package org.anchoranalysis.feature.session.calculator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.feature.calculate.bound.FeatureCalculatorMulti;
import org.anchoranalysis.feature.calculate.bound.FeatureCalculatorSingle;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Adds a cache to a {@link FeatureCalculatorSingle} or {@link FeatureCalculatorMulti}.
 *
 * <p>This caches the results created by the calculators. It is not an internal cache used within
 * the feature-calculation itself.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeatureCalculatorCache {

    private static final int DEFAULT_CACHE_SIZE = 1000;

    public static <T extends FeatureInput> FeatureCalculatorSingle<T> cache(
            FeatureCalculatorSingle<T> calculator) {
        return new CachedSingle<>(calculator, DEFAULT_CACHE_SIZE);
    }

    public static <T extends FeatureInput> FeatureCalculatorSingle<T> cache(
            FeatureCalculatorSingle<T> calculator, int cacheSize) {
        return new CachedSingle<>(calculator, cacheSize);
    }

    public static <T extends FeatureInput> FeatureCalculatorMulti<T> cache(
            FeatureCalculatorMulti<T> calculator) {
        return new CachedMulti<>(calculator, DEFAULT_CACHE_SIZE);
    }

    public static <T extends FeatureInput> FeatureCalculatorMulti<T> cache(
            FeatureCalculatorMulti<T> calculator, int cacheSize) {
        return new CachedMulti<>(calculator, cacheSize);
    }
}
