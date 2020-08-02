package org.anchoranalysis.core.cache;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.functional.CallableWithException;

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
 * Memoizes (caches) a {@link CallableWithException}
 *
 * @author Owen Feehan
 * @param <T> result-type
 * @param <E> exception that is thrown if something goes wrong
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheCall<T, E extends Exception> extends CacheCallBase<T>
        implements CallableWithException<T, E> {

    // START: REQUIRED ARGUMENTS
    private final CallableWithException<T, E> callable;
    // END: REQUIRED ARGUMENTS

    /**
     * Creates a cached-version of a {@link CallableWithException}
     *
     * @param <T> return-type
     * @param <E> exception that may be thrown.
     * @param callable the callable to be cached
     * @return a cached version, with the same interface, and additional functions to monitor
     *     progress, reset etc.
     */
    public static <T, E extends Exception> CacheCall<T, E> of(
            CallableWithException<T, E> callable) {
        return new CacheCall<>(callable);
    }

    @Override
    public T call() throws E {
        return super.call(callable::call);
    }
}
