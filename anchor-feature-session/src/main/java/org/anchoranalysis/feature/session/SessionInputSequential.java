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
import java.util.Set;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.FeatureCalculation;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.calculate.cache.CacheCreator;
import org.anchoranalysis.feature.calculate.cache.CalculateForChild;
import org.anchoranalysis.feature.calculate.cache.CalculationResolver;
import org.anchoranalysis.feature.calculate.cache.ChildCacheName;
import org.anchoranalysis.feature.calculate.cache.FeatureSessionCache;
import org.anchoranalysis.feature.calculate.cache.FeatureSymbolCalculator;
import org.anchoranalysis.feature.calculate.cache.ResolvedCalculation;
import org.anchoranalysis.feature.calculate.cache.SessionInput;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.results.ResultsVector;
import org.anchoranalysis.feature.session.cache.finder.ChildCacheFinder;
import org.anchoranalysis.feature.session.cache.finder.DefaultChildCacheFinder;

/**
 * A feature-input that will be used in a {@link SequentialSession}
 *
 * @author Owen Feehan
 * @param <T> feature-type
 */
public class SessionInputSequential<T extends FeatureInput> implements SessionInput<T> {

    /** Implements operations which should occur using child-caches rather than in the main cache */
    @AllArgsConstructor
    private class ChildCalculator implements CalculateForChild<T> {

        private ChildCacheFinder findChild;

        @Override
        public <S extends FeatureInput> double calculate(
                Feature<S> feature,
                FeatureCalculation<S, T> calculation,
                ChildCacheName childCacheName)
                throws FeatureCalculationException {
            return calculate(
                    feature, SessionInputSequential.this.calculate(calculation), childCacheName);
        }

        @Override
        public <S extends FeatureInput> double calculate(
                Feature<S> feature, S input, ChildCacheName childCacheName)
                throws FeatureCalculationException {

            FeatureSessionCache<S> child = childCacheFor(childCacheName, input);
            return child.calculator()
                    .calculate(
                            feature,
                            new SessionInputSequential<>(
                                    input, child, cacheFactory, findChild.finderForGrandchild()));
        }

        @Override
        public <V extends FeatureInput, U> U calculate(
                ChildCacheName childCacheName,
                V input,
                Function<CalculationResolver<V>, ResolvedCalculation<U, V>> funcCalc)
                throws FeatureCalculationException {

            CalculationResolver<V> childResolver =
                    childCacheFor(childCacheName, input).calculator();
            ResolvedCalculation<U, V> resolvedCalc = funcCalc.apply(childResolver);
            return resolvedCalc.getOrCalculate(input);
        }

        /**
         * Determines which session-cache should be used for a child
         *
         * @throws FeatureCalculationException
         */
        private <V extends FeatureInput> FeatureSessionCache<V> childCacheFor(
                ChildCacheName childName, V input) throws FeatureCalculationException {
            return findChild.childCacheFor(cache, cacheFactory, childName, input);
        }
    }

    private FeatureSessionCache<T> cache;

    private T input;
    private CacheCreator cacheFactory;
    private ChildCalculator childCalc;
    private ChildCacheFinder findChild;

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
        this.childCalc = new ChildCalculator(DefaultChildCacheFinder.instance());
    }

    public SessionInputSequential(T input, CacheCreator cacheFactory, ChildCacheFinder findChild) {
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
            ChildCacheFinder findChild) {
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
    public double calculate(Feature<T> feature) throws FeatureCalculationException {
        return cache.calculator().calculate(feature, this);
    }

    @Override
    public ResultsVector calculate(FeatureList<T> features) throws NamedFeatureCalculateException {
        return cache.calculator().calc(features, this);
    }

    @Override
    public <S> S calculate(FeatureCalculation<S, T> cc) throws FeatureCalculationException {
        return resolver().search(cc).getOrCalculate(input);
    }

    @Override
    public <S> S calculate(ResolvedCalculation<S, T> calculation)
            throws FeatureCalculationException {
        return calculation.getOrCalculate(input);
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
    public CalculateForChild<T> forChild() {
        return childCalc;
    }

    @Override
    public FeatureSessionCache<T> getCache() {
        return cache;
    }
}
