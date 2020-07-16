/* (C)2020 */
package org.anchoranalysis.core.name.store;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.log.CommonContext;

/**
 * Objects shared between different components.
 *
 * <p>This provides a <i>memory</i> between particular processing components, offering a variable
 * state.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class SharedObjects {

    // START REQUIRED ARGUMENTS
    /** Logger and other common configuration */
    @Getter private final CommonContext context;
    // END REQUIRED ARGUMENTS

    /** A set of NamedItemStores, partitioned by Class<?> */
    private Map<Class<?>, NamedProviderStore<?>> setStores = new HashMap<>();

    /**
     * Gets an existing store, or creates a new one
     *
     * @param key unique-identifier for the store
     * @param <T> type of item in store
     * @return an existing-store, or a newly-created one
     */
    @SuppressWarnings("unchecked")
    public <T> NamedProviderStore<T> getOrCreate(Class<?> key) {
        return (NamedProviderStore<T>)
                setStores.computeIfAbsent(
                        key,
                        cls -> new LazyEvaluationStore<>(context.getLogger(), cls.getSimpleName()));
    }
}
