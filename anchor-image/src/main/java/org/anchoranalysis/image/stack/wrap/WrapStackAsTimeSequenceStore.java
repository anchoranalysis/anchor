/* (C)2020 */
package org.anchoranalysis.image.stack.wrap;

import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;

public class WrapStackAsTimeSequenceStore implements NamedProviderStore<TimeSequence> {

    private NamedProviderStore<Stack> namedProvider;
    private int t;

    public WrapStackAsTimeSequenceStore(NamedProviderStore<Stack> namedProvider) {
        this(namedProvider, 0);
    }

    public WrapStackAsTimeSequenceStore(NamedProviderStore<Stack> namedProvider, int t) {
        this.namedProvider = namedProvider;
        this.t = t;
    }

    @Override
    public Optional<TimeSequence> getOptional(String key) throws NamedProviderGetException {
        return namedProvider.getOptional(key).map(TimeSequence::new);
    }

    @Override
    public Set<String> keys() {
        return namedProvider.keys();
    }

    @Override
    public void add(String name, Operation<TimeSequence, OperationFailedException> getter)
            throws OperationFailedException {
        namedProvider.add(name, () -> getter.doOperation().get(t));
    }
}
