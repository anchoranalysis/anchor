/* (C)2020 */
package org.anchoranalysis.core.name.provider;

import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.core.error.OperationFailedException;

public interface NamedProvider<T> {

    /** Retrieves the item if it exists, or throws an exception if it doesn't exist. */
    default T getException(String key) throws NamedProviderGetException {
        return getOptional(key).orElseThrow(() -> NamedProviderGetException.nonExistingItem(key));
    }

    /**
     * Retrieves the item if it exists, or returns empty() if it doesn't exist.
     *
     * <p>Note that a 'key' might still throw an exception for another reason (but never because a
     * particular key is absent).
     */
    Optional<T> getOptional(String key) throws NamedProviderGetException;

    /**
     * Returns a set of keys associated with the provider.
     *
     * <p>There's no guarantee that it refers to all valid keys.
     */
    Set<String> keys();

    /**
     * Gets one element of the provider (arbitrarily)
     *
     * @return one of the elements of the array (arbitrary which one)
     * @throws NamedProviderGetException if the array has no elements
     */
    default T getArbitraryElement() throws OperationFailedException {

        Set<String> keys = keys();

        if (keys.isEmpty()) {
            throw new OperationFailedException("Provider is empty");
        }
        try {
            return getOptional(keys().iterator().next()).get(); // NOSONAR
        } catch (NamedProviderGetException e) {
            throw new OperationFailedException(e.summarize());
        }
    }
}
