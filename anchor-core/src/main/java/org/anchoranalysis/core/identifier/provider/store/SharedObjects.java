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

package org.anchoranalysis.core.identifier.provider.store;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.log.CommonContext;

/**
 * Objects shared between different components.
 *
 * <p>This provides a <i>memory</i> between particular processing components, offering a variable
 * state.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class SharedObjects {

    // START REQUIRED ARGUMENTS
    /** Logger and other common configuration */
    @Getter private final CommonContext context;

    // END REQUIRED ARGUMENTS

    /** A set of NamedItemStores, partitioned by Class<?> */
    private Map<Class<?>, NamedProviderStore<?>> setStores = new HashMap<>();

    /**
     * Gets an existing store, or creates a new one
     *
     * @param key unique-identifier for the store
     * @param <T> type of item in store
     * @return an existing-store, or a newly-created one
     */
    @SuppressWarnings("unchecked")
    public <T> NamedProviderStore<T> getOrCreate(Class<?> key) {
        return (NamedProviderStore<T>)
                setStores.computeIfAbsent(
                        key, cls -> new LazyEvaluationStore<>(cls.getSimpleName()));
    }
}
