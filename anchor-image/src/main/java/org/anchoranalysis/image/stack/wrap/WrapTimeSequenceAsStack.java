/* (C)2020 */
package org.anchoranalysis.image.stack.wrap;

import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;

// Always takes t=0 from the time-sequence
public class WrapTimeSequenceAsStack implements NamedProvider<Stack> {

    private static final int TIME_INDEX = 0;

    private NamedProvider<TimeSequence> namedProvider;

    public WrapTimeSequenceAsStack(NamedProvider<TimeSequence> namedProvider) {
        this.namedProvider = namedProvider;
    }

    @Override
    public Optional<Stack> getOptional(String key) throws NamedProviderGetException {
        return namedProvider.getOptional(key).map(prov -> prov.get(TIME_INDEX));
    }

    @Override
    public Set<String> keys() {
        return namedProvider.keys();
    }
}
