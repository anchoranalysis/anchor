/* (C)2020 */
package org.anchoranalysis.core.name.store;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.name.provider.NamedProvider;

/**
 * A provider, in which items can also be added.
 *
 * <p>So as to allow evaluate to be potentially lazy, an item is not directly added, but a Getter is
 * added that can be calculated on demand.
 *
 * @author Owen Feehan
 * @param <T> item-type in the store
 */
public interface NamedProviderStore<T> extends NamedProvider<T> {

    void add(String name, Operation<T, OperationFailedException> getter)
            throws OperationFailedException;
}
