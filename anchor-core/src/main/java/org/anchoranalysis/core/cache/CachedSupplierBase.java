/*-
 * #%L
 * anchor-core
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
package org.anchoranalysis.core.cache;

import java.util.Optional;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.checked.CheckedSupplier;

/**
 * Base class for functions that memoize (cache) a call to an interface.
 *
 * @author Owen Feehan
 * @param <T> result-type
 */
@NoArgsConstructor
public abstract class CachedSupplierBase<T> {

    private Optional<T> result = Optional.empty();

    /**
     * Has the function already been evaluated?
     *
     * <p>i.e. does a value already exist in the cache.
     *
     * @return true iff a value exists in the cache.
     */
    public synchronized boolean isEvaluated() {
        return result.isPresent();
    }

    /**
     * Gets the value supplied by {@code supplier} via the cache if it exists, or otherwise via the
     * supplier.
     *
     * <p>The value is then cached, and the object is considered as <i>evaluated</i>.
     *
     * @param <E> an exception that can be thrown by {@code supplier}.
     * @param supplier the operation used to create the value.
     * @return the value, either created or from the cache.
     * @throws E if thrown by {@code supplier}.
     */
    protected synchronized <E extends Exception> T call(CheckedSupplier<T, E> supplier) throws E {
        if (!isEvaluated()) {
            result = Optional.of(supplier.get());
        }
        return result.get();    //NOSONAR
    }

    /** Ensures the object is unevaluated, deleting any cached result if it exists. */
    public synchronized void reset() {
        result = Optional.empty();
    }
}
