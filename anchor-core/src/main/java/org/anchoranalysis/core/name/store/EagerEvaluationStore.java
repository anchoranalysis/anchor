/* (C)2020 */
package org.anchoranalysis.core.name.store;

import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.value.SimpleNameValue;

/**
 * Evaluates items via their Getter as soon as they are added
 *
 * @author Owen Feehan
 * @param <T> item-type in the store
 */
public class EagerEvaluationStore<T> implements NamedProviderStore<T> {

    private NameValueSet<T> delegate = new NameValueSet<>();

    @Override
    public Set<String> keys() {
        return delegate.keys();
    }

    @Override
    public void add(String name, Operation<T, OperationFailedException> getter)
            throws OperationFailedException {

        SimpleNameValue<T> item = new SimpleNameValue<>(name, getter.doOperation());

        delegate.add(item);
    }

    @Override
    public Optional<T> getOptional(String key) {
        return delegate.getOptional(key);
    }
}
