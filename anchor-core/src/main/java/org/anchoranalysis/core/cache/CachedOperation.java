package org.anchoranalysis.core.cache;

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
 * @author Owen Feehan
 * @param <T> result-type
 * @param <E> exception that is thrown if something goes wrong
 */
public class CachedOperation<T, E extends Exception> implements CallableWithException<T, E> {

    private T result;
    private boolean done;
    private CallableWithException<T, E> callable;

    public static <T, E extends Exception> CachedOperation<T, E> of(CallableWithException<T, E> callable) {
        return new CachedOperation<>(callable);
    }
    
    public static <T, E extends Exception> CachedOperation<T, E> of(CallableWithException<T, E> callable, T result) {
        return new CachedOperation<>(callable, result);
    }
    
    /** Constructor - with no result calculated yet */
    private CachedOperation(CallableWithException<T, E> callable) {
        this.callable = callable;
        result = null;
        done = false;
    }

    /**
     * Constructor - with result calculated (if not null)
     *
     * <p>
     *
     * @param result if non-null the result of the cached operation.
     */
    private CachedOperation(CallableWithException<T, E> callable, T result) {
        this.callable = callable;
        this.result = result;
        this.done = result != null;
    }

    @Override
    public synchronized T call() throws E {

        if (!done) {
            result = execute();
            done = true;
        }
        return result;
    }
    
    public synchronized void assignFrom(T result) {
        this.result = result;
        this.done = result != null;
    }

    public synchronized void assignFrom(CachedOperation<T, E> src) {
        this.result = src.result;
        this.done = src.done;
    }

    public synchronized void reset() {
        done = false;
        result = null;
    }

    public synchronized boolean isDone() {
        return done;
    }

    protected T execute() throws E {
        return callable.call();
    }

    public T getResult() {
        return result;
    }
}
