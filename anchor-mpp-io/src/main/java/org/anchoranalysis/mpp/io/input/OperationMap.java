/* (C)2020 */
package org.anchoranalysis.mpp.io.input;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.name.store.NamedProviderStore;

/**
 * Stores objects as operations
 *
 * @param T object-type
 */
public class OperationMap<T> implements MultiInputSubMap<T> {

    private Map<String, Operation<T, OperationFailedException>> map = new HashMap<>();

    @Override
    public void add(String name, Operation<T, OperationFailedException> op) {
        map.put(name, op);
    }

    @Override
    public void addToStore(NamedProviderStore<T> namedStore) throws OperationFailedException {
        for (Entry<String, Operation<T, OperationFailedException>> entry : map.entrySet()) {
            namedStore.add(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Operation<T, OperationFailedException> get(String name) throws OperationFailedException {
        Operation<T, OperationFailedException> ret = map.get(name);
        if (ret == null) {
            throw new OperationFailedException(String.format("Cannot find key '%s'", name));
        }
        return ret;
    }

    public Map<String, Operation<T, OperationFailedException>> getMap() {
        return map;
    }
}
