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

import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.function.CheckedSupplier;

/**
 * Base class for functions that memoize (cache) a call to an interface
 *
 * @author Owen Feehan
 * @param <T> result-type
 */
@NoArgsConstructor
public abstract class CachedSupplierBase<T> {

    private T result;
    private boolean evaluated = false;

    public synchronized void assignFrom(CachedSupplierBase<T> source) {
        this.result = source.result;
        this.evaluated = source.evaluated;
    }

    public synchronized void reset() {
        evaluated = false;
        result = null;
    }

    public synchronized boolean isEvaluated() {
        return evaluated;
    }

    protected synchronized <E extends Exception> T call(CheckedSupplier<T, E> supplier)
            throws E {

        if (!evaluated) {
            result = supplier.get();
            evaluated = true;
        }
        return result;
    }
}
