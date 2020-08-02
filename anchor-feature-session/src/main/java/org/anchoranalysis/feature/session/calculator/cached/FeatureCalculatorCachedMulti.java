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

package org.anchoranalysis.feature.session.calculator.cached;

import java.util.Optional;
import org.anchoranalysis.core.cache.LRUCache;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.NamedFeatureCalculationException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;

/**
 * A {@link FeatureCalculatorMulti} but calculations are cached to avoid repetition if equal {@link
 * FeatureInput} are passed.
 *
 * @author Owen Feehan
 */
public class FeatureCalculatorCachedMulti<T extends FeatureInput>
        implements FeatureCalculatorMulti<T> {

    private static final int DEFAULT_CACHE_SIZE = 1000;

    private final FeatureCalculatorMulti<T> source;
    private final LRUCache<T, ResultsVector> cacheResults;

    // We update this every time so it matches whatever is passed to calcSuppressErrors
    private Optional<ErrorReporter> errorReporter = Optional.empty();

    /**
     * Creates a feature-calculator with a new cache
     *
     * @param source the underlying feature-calculator to use for calculating unknown results
     */
    public FeatureCalculatorCachedMulti(FeatureCalculatorMulti<T> source) {
        this(source, DEFAULT_CACHE_SIZE);
    }

    /**
     * Creates a feature-calculator with a new cache
     *
     * @param source the underlying feature-calculator to use for calculating unknown results
     * @param cacheSize size of cache to use
     */
    public FeatureCalculatorCachedMulti(FeatureCalculatorMulti<T> source, int cacheSize) {
        this.source = source;
        this.cacheResults = new LRUCache<>(cacheSize, this::calcInsideCache);
    }

    @Override
    public ResultsVector calcSuppressErrors(T input, ErrorReporter errorReporter) {
        this.errorReporter = Optional.of(errorReporter); // Suppress errors in cache
        try {
            return cacheResults.get(input);
        } catch (GetOperationFailedException e) {
            errorReporter.recordError(FeatureCalculatorCachedMulti.class, e.getCause());
            return createNaNVector(e);
        }
    }

    @Override
    public ResultsVector calc(T input) throws NamedFeatureCalculationException {
        this.errorReporter = Optional.empty(); // Do not suppress errors in cache
        try {
            return cacheResults.get(input);
        } catch (GetOperationFailedException e) {
            throw new NamedFeatureCalculationException(e.getKey(), e.getMessage());
        }
    }

    @Override
    public ResultsVector calc(T input, FeatureList<T> featuresSubset)
            throws NamedFeatureCalculationException {
        throw new NamedFeatureCalculationException(
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
        ResultsVector rv = new ResultsVector(source.sizeFeatures());
        rv.setErrorAll(e);
        return rv;
    }

    private ResultsVector calcInsideCache(T index) throws NamedFeatureCalculationException {
        if (errorReporter.isPresent()) {
            return source.calcSuppressErrors(index, errorReporter.get());
        } else {
            return source.calc(index);
        }
    }
}
