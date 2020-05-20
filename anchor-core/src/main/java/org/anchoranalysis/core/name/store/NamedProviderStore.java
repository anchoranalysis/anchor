package org.anchoranalysis.core.name.store;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.name.provider.INamedProvider;

/**
 * A provider, in which items can also be added.
 * 
 * So as to allow evaluate to be potentially lazy, an item is not directly added, but a Getter is
 *  added that can be calculated on demand.
 * 
 * @author Owen Feehan
 *
 * @param <T> item-type in the store
 */
public abstract class NamedProviderStore<T> implements INamedProvider<T> {

	public abstract void add( String name, Operation<T,OperationFailedException> getter ) throws OperationFailedException;
}
