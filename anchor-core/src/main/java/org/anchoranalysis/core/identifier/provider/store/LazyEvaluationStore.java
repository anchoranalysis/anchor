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
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;

/**
 * Items are evaluated only when they are first needed. The value is thereafter stored.
 *
 * @author Owen Feehan
 * @param <T> item-type in the store
 */
@RequiredArgsConstructor
public class LazyEvaluationStore<T> implements NamedProviderStore<T> {

    // START REQUIRED ARGUMENTS
    private final String storeDisplayName;
    // END REQUIRED ARGUMENTS

    private HashMap<String, CachedSupplier<T, OperationFailedException>> map = new HashMap<>();

    @Override
    public T getException(String key) throws NamedProviderGetException {
        return getOptional(key)
                .orElseThrow(
                        () -> NamedProviderGetException.nonExistingItem(key, storeDisplayName));
    }

    @Override
    public Optional<T> getOptional(String key) throws NamedProviderGetException {
        try {
            return OptionalUtilities.map(Optional.ofNullable(map.get(key)), CachedSupplier::get);
        } catch (Exception e) {
            throw NamedProviderGetException.wrap(key, e);
        }
    }

    // We only refer to
    public Set<String> keysEvaluated() {
        HashSet<String> keysUsed = new HashSet<>();
        for (Entry<String, CachedSupplier<T, OperationFailedException>> entry : map.entrySet()) {
            if (entry.getValue().isEvaluated()) {
                keysUsed.add(entry.getKey());
            }
        }
        return keysUsed;
    }

    // All keys that it is possible to evaluate
    @Override
    public Set<String> keys() {
        return map.keySet();
    }

    @Override
    public void add(String identifier, StoreSupplier<T> supplier) throws OperationFailedException {
        map.put(identifier, StoreSupplier.cacheResettable(supplier));
    }
}
