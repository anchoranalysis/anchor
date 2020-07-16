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
/* (C)2020 */
package org.anchoranalysis.bean;

import java.util.Optional;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.CreateException;

/**
 * Utility function to create Optional<> from providers, which may be null
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OptionalFactory {

    /**
     * Creates from a provider if non-null
     *
     * @param <T> type of optional as created by the provider
     * @param provider a provider or null (if it doesn't exist)
     * @return the result of the create() option if provider is non-NULL, otherwise {@link
     *     Optional.empty()}
     * @throws CreateException
     */
    public static <T> Optional<T> create(Provider<T> provider) throws CreateException {
        if (provider != null) {
            return Optional.of(provider.create());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Creates only if a boolean flag is TRUE, otherwise returns empty
     *
     * @param <T> type of optional
     * @param toggle boolean flag
     * @param createFunc a function to create a value T, only used if the boolean flag is TRUE
     * @return a present or empty optional depending on the flag
     */
    public static <T> Optional<T> create(boolean toggle, Supplier<T> createFunc) {
        if (toggle) {
            return Optional.of(createFunc.get());
        } else {
            return Optional.empty();
        }
    }
}
