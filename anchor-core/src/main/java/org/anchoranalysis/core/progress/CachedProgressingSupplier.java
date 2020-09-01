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

package org.anchoranalysis.core.progress;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.cache.CachedSupplierBase;

/**
 * Memoizes (caches) a call to {@link CheckedProgressingSupplier}
 *
 * @author Owen Feehan
 * @param <T> result-type
 * @param <E> exception thrown during operation
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CachedProgressingSupplier<T, E extends Exception> extends CachedSupplierBase<T>
        implements CheckedProgressingSupplier<T, E> {

    // START: REQUIRED ARGUMENTS
    private final CheckedProgressingSupplier<T, E> supplier;
    // END: REQUIRED ARGUMENTS

    /**
     * Creates a cached-version of a {@link CheckedProgressingSupplier}
     *
     * @param <T> return-type
     * @param <E> exception that may be thrown.
     * @param supplier the supplier to be cached
     * @return a cached version, with the same interface, and additional functions to monitor
     *     progress, reset etc.
     */
    public static <T, E extends Exception> CachedProgressingSupplier<T, E> cache(
            CheckedProgressingSupplier<T, E> supplier) {
        return new CachedProgressingSupplier<>(supplier);
    }

    @Override
    public T get(ProgressReporter progressReporter) throws E {
        return super.call(() -> supplier.get(progressReporter));
    }
}
