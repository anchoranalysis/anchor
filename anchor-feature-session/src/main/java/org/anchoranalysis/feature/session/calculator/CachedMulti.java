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

package org.anchoranalysis.feature.session.calculator;

import java.util.Optional;
import org.anchoranalysis.core.cache.LRUCache;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.calculate.bound.FeatureCalculatorMulti;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.results.ResultsVector;

/**
 * A {@link FeatureCalculatorMulti} but calculations are cached to avoid repetition if equal {@link
 * FeatureInput} are passed.
 *
 * @author Owen Feehan
 */
class CachedMulti<T extends FeatureInput> implements FeatureCalculatorMulti<T> {

    private final FeatureCalculatorMulti<T> source;
    private final LRUCache<T, ResultsVector> cacheResults;

    /**
     * We update this every time so it matches whatever is passed to {@code
     * #calculateSuppressErrors(FeatureInput, ErrorReporter)}
     */
    private Optional<ErrorReporter> errorReporter = Optional.empty();

    /**
     * Creates a feature-calculator with a new cache.
     *
     * @param source the underlying feature-calculator to use for calculating unknown results.
     * @param cacheSize size of cache to use.
     */
    public CachedMulti(FeatureCalculatorMulti<T> source, int cacheSize) {
        this.source = source;
        this.cacheResults = new LRUCache<>(cacheSize, this::calculateInsideCache);
    }

    @Override
    public ResultsVector calculateSuppressErrors(T input, ErrorReporter errorReporter) {
        this.errorReporter = Optional.of(errorReporter); // Suppress errors in cache
        try {
            return cacheResults.get(input);
        } catch (GetOperationFailedException e) {
            errorReporter.recordError(CachedMulti.class, e.getCause());
            return createNaNVector(e);
        }
    }

    @Override
    public ResultsVector calculate(T input) throws NamedFeatureCalculateException {
        this.errorReporter = Optional.empty(); // Do not suppress errors in cache
        try {
            return cacheResults.get(input);
        } catch (GetOperationFailedException e) {
            throw new NamedFeatureCalculateException(e.getKey(), e.getMessage());
        }
    }

    @Override
    public ResultsVector calculate(T input, FeatureList<T> featuresSubset)
            throws NamedFeatureCalculateException {
        throw new NamedFeatureCalculateException(
                "This operation is not supported for subsets of features");
    }

    @Override
    public int sizeFeatures() {
        return source.sizeFeatures();
    }

    /** Checks whether the cache contains a particular input already. Useful for tests. */
    public boolean has(T key) {
        return cacheResults.has(key);
    }

    /** Number of items currently in the cache */
    public long sizeCurrentLoad() {
        return cacheResults.sizeCurrentLoad();
    }

    /** Return a vector with all NaNs */
    private ResultsVector createNaNVector(GetOperationFailedException e) {
        ResultsVector results = new ResultsVector(source.sizeFeatures());
        results.setErrorAll(e);
        return results;
    }

    private ResultsVector calculateInsideCache(T index) throws NamedFeatureCalculateException {
        if (errorReporter.isPresent()) {
            return source.calculateSuppressErrors(index, errorReporter.get());
        } else {
            return source.calculate(index);
        }
    }
}
