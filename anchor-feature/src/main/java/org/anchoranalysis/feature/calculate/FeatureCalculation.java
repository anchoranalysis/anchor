/*-
 * #%L
 * anchor-feature
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

package org.anchoranalysis.feature.calculate;

import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.feature.calculate.cache.ResettableCalculation;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Caches the result of a feature, until {@link #invalidate()} is called.
 *
 * <p>Implements an equivalence-relation via {@code equals()} that checks if two
 * feature-calculations are identical. This allows the user to search through a collection of {@link
 * FeatureCalculation} to find one with identical parameters and re-use it.
 *
 * <p>IMPORTANT NOTE: It is therefore important to make sure every class has a well-defined {@code
 * equals()} and {@code hashCode()}.
 *
 * @author Owen Feehan
 * @param <S> result-type of the calculation
 * @param <T> feature input-type
 */
public abstract class FeatureCalculation<S, T extends FeatureInput>
        implements ResettableCalculation {

    private T input;

    // We delegate the actualy execution of the cache
    private CachedSupplier<S, FeatureCalculationException> delegate =
            CachedSupplier.cache(() -> FeatureCalculation.this.execute(input));

    /**
     * Executes the operation and returns a result, either by doing the calculation, or retrieving a
     * cached-result from previously.
     *
     * @param input If there is no cached-value, and the calculation occurs, this input is used.
     *     Otherwise ignored.
     * @return the result of the calculation
     * @throws FeatureCalculationException if the calculation cannot finish for whatever reason
     */
    public synchronized S getOrCalculate(T input) throws FeatureCalculationException {
        // The input should be equal to the existing input, but this is not checked
        // as it would add computional cost. Consider an assert with the
        // checkParamsMatchesInput(input)
        // function for debugging.
        assignInitialization(input);
        return delegate.get();
    }

    @Override
    public abstract boolean equals(Object other);

    @Override
    public abstract int hashCode();

    public boolean hasCachedCalculation() {
        return delegate.isEvaluated();
    }

    @Override
    public synchronized void invalidate() {
        delegate.reset();
        this.input = null; // Just to be clean, release memory, before the next getOrCalculate
    }

    /**
     * This performs the actual calculation when needed. It should only be called once, until
     * invalidate() is called.
     */
    protected abstract S execute(T input) throws FeatureCalculationException;

    private synchronized void assignInitialization(T input) {
        this.input = input;
    }

    /**
     * A check that if params are already set, any new inputs must be identical.
     *
     * <p>This method is unused, but delibiberately left for debugging in {@link #getOrCalculate}.
     */
    @SuppressWarnings("unused")
    private boolean checkParamsMatchesInput(T input) {
        if (hasCachedCalculation() && input != null && !input.equals(this.input)) {
            throw new AnchorFriendlyRuntimeException(
                    "This feature already has been used, its cache is already set to different params");
        }
        return true;
    }
}
