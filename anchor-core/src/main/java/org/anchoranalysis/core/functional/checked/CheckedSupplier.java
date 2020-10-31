package org.anchoranalysis.core.functional.checked;

import java.util.function.Supplier;
import org.anchoranalysis.core.progress.CheckedProgressingSupplier;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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
 * Like {@link Supplier} but can also throw an exception.
 *
 * @author Owen Feehan
 * @param <T> type of object to supply
 * @param <E> exception-type if supplying fails
 */
@FunctionalInterface
public interface CheckedSupplier<T, E extends Exception> {

    /**
     * Applies a supplier like with {@link Supplier#get}.
     *
     * @return the supplied object.
     * @throws E an exception that may be thrown
     */
    T get() throws E;

    /** An interface to a similar supplier that uses a progress-reporter */
    default CheckedProgressingSupplier<T, E> progressing() {
        return progressReporter -> get();
    }
}