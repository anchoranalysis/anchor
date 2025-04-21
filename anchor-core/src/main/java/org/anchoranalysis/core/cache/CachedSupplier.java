package org.anchoranalysis.core.cache;

import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.functional.checked.CheckedSupplier;

/*
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

/**
 * Memoizes (caches) a {@link CheckedSupplier}.
 *
 * @author Owen Feehan
 * @param <T> result-type
 * @param <E> exception that is thrown if something goes wrong
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CachedSupplier<T, E extends Exception> extends CachedSupplierBase<T>
        implements CheckedSupplier<T, E> {

    // START: REQUIRED ARGUMENTS
    /** Supplies the value to be calculated and cached. */
    private final CheckedSupplier<T, E> supplier;

    // END: REQUIRED ARGUMENTS

    /**
     * Creates a cached-version of a {@link Supplier}.
     *
     * @param <T> return-type
     * @param <E> exception that will never be thrown, but is parameterized to match the
     *     destination-type..
     * @param suppplier supplies the value to be calculated and cached.
     * @return a cached version, with the same interface, and additional functions to monitor
     *     progress, reset etc.
     */
    public static <T, E extends Exception> CachedSupplier<T, E> cache(Supplier<T> suppplier) {
        return new CachedSupplier<>(suppplier::get);
    }

    /**
     * Creates a cached-version of a {@link CheckedSupplier}.
     *
     * @param <T> return-type
     * @param <E> exception that may be thrown.
     * @param suppplier supplies the value to be calculated and cached.
     * @return a cached version, with the same interface, and additional functions to monitor
     *     progress, reset etc.
     */
    public static <T, E extends Exception> CachedSupplier<T, E> cacheChecked(
            CheckedSupplier<T, E> suppplier) {
        return new CachedSupplier<>(suppplier);
    }

    @Override
    public T get() throws E {
        return super.call(supplier::get);
    }
}
