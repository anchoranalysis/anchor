/*-
 * #%L
 * anchor-bean
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

package org.anchoranalysis.bean;

import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.provider.Provider;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.functional.checked.CheckedSupplier;

/**
 * Utility functions to create {@link Optional} from nullable providers or boolean flags.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OptionalFactory {

    /**
     * Creates from a provider if non-null.
     *
     * @param <T> type of optional as created by the provider
     * @param provider a provider or null (if it doesn't exist)
     * @return the result of the create() option if provider is non-null, otherwise {@link Optional}
     * @throws CreateException
     */
    public static <T> Optional<T> create(@Nullable Provider<T> provider) throws CreateException {
        if (provider != null) {
            return Optional.of(provider.create());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Creates only if a boolean flag is true, otherwise returns {@code Optional.empty()}.
     *
     * @param <T> type of optional
     * @param flag boolean flag
     * @param supplier a function to create a value T only called if {@code flag} is true
     * @return an optional that is defined or empty depending on the flag
     */
    public static <T> Optional<T> create(boolean flag, Supplier<T> supplier) {
        if (flag) {
            return Optional.of(supplier.get());
        } else {
            return Optional.empty();
        }
    }
    
    /**
     * Like {@link #create} but accepts a supplier that throws a checked/exception.
     *
     * @param <T> type of optional
     * @param <E> the checked exception
     * @param flag boolean flag
     * @param supplier a function to create a value T only called if {@code flag} is true
     * @return an optional that is defined or empty depending on the flag
     * @throws E if <code>supplier</code> throws it
     */
    public static <T,E extends Exception> Optional<T> createWithException(boolean flag, CheckedSupplier<T,E> supplier) throws E {
        if (flag) {
            return Optional.of(supplier.get());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Creates {@code Optional.empty()} for an empty string, or otherwise {@code Optional.of()}.
     *
     * @param string the string (possibly empty)
     * @return the optional
     */
    public static Optional<String> create(String string) {
        if (!string.isEmpty()) {
            return Optional.of(string);
        } else {
            return Optional.empty();
        }
    }
}
