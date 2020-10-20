/*-
 * #%L
 * anchor-mpp-io
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

package org.anchoranalysis.mpp.io.input;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.StoreSupplier;

/**
 * Stores objects as operations
 *
 * @param <T> object-type
 */
public class OperationMap<T> implements MultiInputSubMap<T> {

    private Map<String, StoreSupplier<T>> map = new HashMap<>();

    @Override
    public void add(String name, StoreSupplier<T> op) {
        map.put(name, op);
    }

    @Override
    public void addToStore(NamedProviderStore<T> namedStore) throws OperationFailedException {
        for (Entry<String, StoreSupplier<T>> entry : map.entrySet()) {
            namedStore.add(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public StoreSupplier<T> get(String name) throws OperationFailedException {
        StoreSupplier<T> ret = map.get(name);
        if (ret == null) {
            throw new OperationFailedException(String.format("Cannot find key '%s'", name));
        }
        return ret;
    }

    public Map<String, StoreSupplier<T>> getMap() {
        return map;
    }
}
