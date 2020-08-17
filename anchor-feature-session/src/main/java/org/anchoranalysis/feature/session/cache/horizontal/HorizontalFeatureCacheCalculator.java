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

package org.anchoranalysis.feature.session.cache.horizontal;

import java.util.Collection;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.cache.calculate.CacheableCalculationMap;
import org.anchoranalysis.feature.cache.calculate.FeatureCalculation;
import org.anchoranalysis.feature.cache.calculate.FeatureSessionCacheCalculator;
import org.anchoranalysis.feature.cache.calculate.ResolvedCalculation;
import org.anchoranalysis.feature.cache.calculate.ResolvedCalculationMap;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * @author Owen Feehan
 * @param <T> feature-input
 */
class HorizontalFeatureCacheCalculator<T extends FeatureInput>
        implements FeatureSessionCacheCalculator<T> {

    private FeatureSessionCacheCalculator<T> delegate;
    private FeatureResultMap<T> map;
    private Collection<String> ignorePrefixes;

    public HorizontalFeatureCacheCalculator(
            FeatureSessionCacheCalculator<T> delegate,
            FeatureResultMap<T> map,
            Collection<String> ignorePrefixes) {
        this.delegate = delegate;
        this.map = map;
        this.ignorePrefixes = ignorePrefixes;
    }

    private Double calculateAndAdd(Feature<T> feature, SessionInput<T> input)
            throws FeatureCalculationException {
        Double result = delegate.calculate(feature, input);
        map.add(feature, resolveNameFeature(feature), result);
        return result;
    }

    private String resolveNameFeature(Feature<T> feature) {
        String id = feature.getCustomName();
        if (id != null && !id.isEmpty()) {
            return resolveFeatureIdentifier(id);
        } else {
            return id;
        }
    }

    @Override
    public double calculate(Feature<T> feature, SessionInput<T> input)
            throws FeatureCalculationException {

        // if there's no custom name, then we don't consider caching
        if (feature.getCustomName() == null || feature.getCustomName().isEmpty()) {
            return delegate.calculate(feature, input);
        }

        // Otherwise we save the result, and cache it for next time
        Double result = map.getResultFor(feature);
        if (result == null) {
            result = calculateAndAdd(feature, input);
        }
        return result;
    }

    @Override
    public <U> ResolvedCalculation<U, T> search(FeatureCalculation<U, T> cc) {
        return delegate.search(cc);
    }

    @Override
    public <S, U> ResolvedCalculationMap<S, T, U> search(
            CacheableCalculationMap<S, T, U, FeatureCalculationException> cc) {
        return delegate.search(cc);
    }

    @Override
    public String resolveFeatureIdentifier(String identifier) {

        // If any of the prefixes exist, they are removed
        for (String prefix : ignorePrefixes) {
            if (identifier.startsWith(prefix)) {
                String idPrefixRemoved = identifier.substring(prefix.length());
                return delegate.resolveFeatureIdentifier(idPrefixRemoved);
            }
        }
        return delegate.resolveFeatureIdentifier(identifier);
    }

    @Override
    public double calculateFeatureByIdentifier(String id, SessionInput<T> input)
            throws FeatureCalculationException {

        // Let's first check if it's in our cache
        Double result = map.getResultFor(id);

        if (result != null) {
            return result;
        }

        // If it's not there, then let's find the feature we need to calculate from our list
        Feature<T> feature = map.getFeatureFor(id);

        if (feature != null) {
            return calculateAndAdd(feature, input);
        } else {
            // We cannot find our feature throw an error, try the delegate
            return delegate.calculateFeatureByIdentifier(id, input);
        }
    }
}
