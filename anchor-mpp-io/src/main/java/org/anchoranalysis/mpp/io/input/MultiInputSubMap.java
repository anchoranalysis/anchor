/* (C)2020 */
package org.anchoranalysis.mpp.io.input;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.name.store.NamedProviderStore;

/**
 * a sub-item of multi-input which: 1. involves a map of objects of type T 2. can have its contents
 * copied into a NamedProviderStore
 *
 * @param T object-type
 */
public interface MultiInputSubMap<T> {

    /** Adds an entry to the map */
    void add(String name, Operation<T, OperationFailedException> op);

    /** Copies all the existing entries into a NamedProvierStore */
    void addToStore(NamedProviderStore<T> namedStore) throws OperationFailedException;

    /** Returns null if non-existent */
    Operation<T, OperationFailedException> get(String name) throws OperationFailedException;
}
