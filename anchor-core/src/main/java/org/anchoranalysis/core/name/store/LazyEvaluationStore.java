/* (C)2020 */
package org.anchoranalysis.core.name.store;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.cache.WrapOperationAsCached;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.cachedgetter.ProfiledCachedGetter;

/**
 * Items are evaluated only when they are first needed. The value is thereafter stored.
 *
 * @author Owen Feehan
 * @param <T> item-type in the store
 */
@RequiredArgsConstructor
public class LazyEvaluationStore<T> implements NamedProviderStore<T> {

    // START REQUIRED ARGUMENTS
    private final Logger logger;
    private final String storeDisplayName;
    // END REQUIRED ARGUMENTS

    private HashMap<String, WrapOperationAsCached<T, OperationFailedException>> map =
            new HashMap<>();

    @Override
    public T getException(String key) throws NamedProviderGetException {
        return getOptional(key)
                .orElseThrow(
                        () -> NamedProviderGetException.nonExistingItem(key, storeDisplayName));
    }

    @Override
    public Optional<T> getOptional(String key) throws NamedProviderGetException {
        try {
            return OptionalUtilities.map(
                    Optional.ofNullable(map.get(key)), WrapOperationAsCached::doOperation);
        } catch (Exception e) {
            throw NamedProviderGetException.wrap(key, e);
        }
    }

    // We only refer to
    public Set<String> keysEvaluated() {
        HashSet<String> keysUsed = new HashSet<>();
        for (Entry<String, WrapOperationAsCached<T, OperationFailedException>> entry :
                map.entrySet()) {
            if (entry.getValue().isDone()) {
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
    public void add(String name, Operation<T, OperationFailedException> getter)
            throws OperationFailedException {
        map.put(name, new ProfiledCachedGetter<>(getter, name, storeDisplayName, logger));
    }
}
