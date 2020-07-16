/* (C)2020 */
package org.anchoranalysis.feature.session;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.cache.FeatureSymbolCalculator;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.cache.calculation.CacheCreator;
import org.anchoranalysis.feature.cache.calculation.CalcForChild;
import org.anchoranalysis.feature.cache.calculation.CalculationResolver;
import org.anchoranalysis.feature.cache.calculation.FeatureCalculation;
import org.anchoranalysis.feature.cache.calculation.FeatureSessionCache;
import org.anchoranalysis.feature.cache.calculation.ResolvedCalculation;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.strategy.child.DefaultFindChildStrategy;
import org.anchoranalysis.feature.session.strategy.child.FindChildStrategy;

/**
 * A feature-input that will be used in a {@link SequentialSession}
 *
 * @author Owen Feehan
 * @param <T> feature-type
 */
public class SessionInputSequential<T extends FeatureInput> implements SessionInput<T> {

    /** Implements operations which should occur using child-caches rather than in the main cache */
    private class ChildCalculator implements CalcForChild<T> {

        private FindChildStrategy findChild;

        public ChildCalculator(FindChildStrategy findChild) {
            super();
            this.findChild = findChild;
        }

        @Override
        public <S extends FeatureInput> double calc(
                Feature<S> feature,
                FeatureCalculation<S, T> calcInput,
                ChildCacheName childCacheName)
                throws FeatureCalcException {
            return calc(feature, SessionInputSequential.this.calc(calcInput), childCacheName);
        }

        @Override
        public <S extends FeatureInput> double calc(
                Feature<S> feature, S input, ChildCacheName childCacheName)
                throws FeatureCalcException {

            FeatureSessionCache<S> child = childCacheFor(childCacheName, input);
            return child.calculator()
                    .calc(
                            feature,
                            new SessionInputSequential<S>(
                                    input, child, cacheFactory, findChild.strategyForGrandchild()));
        }

        @Override
        public <V extends FeatureInput, U> U calc(
                ChildCacheName childCacheName,
                V input,
                Function<CalculationResolver<V>, ResolvedCalculation<U, V>> funcCalc)
                throws FeatureCalcException {

            CalculationResolver<V> childResolver =
                    childCacheFor(childCacheName, input).calculator();
            ResolvedCalculation<U, V> resolvedCalc = funcCalc.apply(childResolver);
            return resolvedCalc.getOrCalculate(input);
        }

        /**
         * Determines which session-cache should be used for a child
         *
         * @throws FeatureCalcException
         */
        private <V extends FeatureInput> FeatureSessionCache<V> childCacheFor(
                ChildCacheName childName, V input) throws FeatureCalcException {
            return findChild.childCacheFor(cache, cacheFactory, childName, input);
        }
    }

    private FeatureSessionCache<T> cache;

    private T input;
    private CacheCreator cacheFactory;
    private ChildCalculator childCalc;
    private FindChildStrategy findChild;

    /**
     * Constructor
     *
     * @param input input to features
     * @param cacheFactory
     */
    public SessionInputSequential(T input, CacheCreator cacheFactory) {
        this.input = input;
        this.cacheFactory = cacheFactory;

        // Deliberately two lines, as it needs an explicitly declared type for the template type
        // inference to work
        this.cache = cacheFactory.create(input.getClass());
        this.childCalc = new ChildCalculator(DefaultFindChildStrategy.instance());
    }

    public SessionInputSequential(T input, CacheCreator cacheFactory, FindChildStrategy findChild) {
        this.input = input;
        this.cacheFactory = cacheFactory;

        // Deliberately two lines, as it needs an explicitly declared type for the template type
        // inference to work
        this.cache = cacheFactory.create(input.getClass());
        this.childCalc = new ChildCalculator(findChild);
        this.findChild = findChild;
    }

    /**
     * Constructor when a cache is already identified
     *
     * @param input feature-input
     * @param cache the cache to associate with the session-input
     * @param cacheFactory creates new caches
     * @param findChild
     */
    SessionInputSequential(
            T input,
            FeatureSessionCache<T> cache,
            CacheCreator cacheFactory,
            FindChildStrategy findChild) {
        this.input = input;
        this.cacheFactory = cacheFactory;
        this.cache = cache;
        this.childCalc = new ChildCalculator(findChild);
    }

    /**
     * Replaces existing input with new input
     *
     * @param input new parameters which will replace existing ones
     */
    public void replaceInput(T input) {

        Optional<Set<ChildCacheName>> exceptedChildren = this.findChild.cachesToAvoidInvalidating();
        if (exceptedChildren.isPresent()) {
            // Invalidate everything apart from particular exceptions
            cache.invalidateExcept(exceptedChildren.get());
        } else {
            // Invalidate everything
            cache.invalidate();
        }
        this.input = input;
    }

    @Override
    public T get() {
        return input;
    }

    @Override
    public double calc(Feature<T> feature) throws FeatureCalcException {
        return cache.calculator().calc(feature, this);
    }

    @Override
    public ResultsVector calc(FeatureList<T> features) throws FeatureCalcException {
        return cache.calculator().calc(features, this);
    }

    @Override
    public <S> S calc(FeatureCalculation<S, T> cc) throws FeatureCalcException {
        return resolver().search(cc).getOrCalculate(input);
    }

    @Override
    public <S> S calc(ResolvedCalculation<S, T> cc) throws FeatureCalcException {
        return cc.getOrCalculate(input);
    }

    @Override
    public CalculationResolver<T> resolver() {
        return cache.calculator();
    }

    @Override
    public FeatureSymbolCalculator<T> bySymbol() {
        return cache.calculator();
    }

    @Override
    public CalcForChild<T> forChild() {
        return childCalc;
    }

    @Override
    public FeatureSessionCache<T> getCache() {
        return cache;
    }
}
