/* (C)2020 */
package org.anchoranalysis.image.stack.wrap;

import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;

public class WrapStackAsTimeSequence implements NamedProvider<TimeSequence> {

    private NamedProvider<Stack> namedProvider;

    public WrapStackAsTimeSequence(NamedProvider<Stack> namedProvider) {
        this.namedProvider = namedProvider;
    }

    @Override
    public Optional<TimeSequence> getOptional(String key) throws NamedProviderGetException {
        return namedProvider.getOptional(key).map(TimeSequence::new);
    }

    @Override
    public Set<String> keys() {
        return namedProvider.keys();
    }
}
