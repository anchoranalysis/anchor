/* (C)2020 */
package org.anchoranalysis.bean;

import java.util.Optional;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.CreateException;

/**
 * Utility function to create Optional<> from providers, which may be null
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OptionalFactory {

    /**
     * Creates from a provider if non-null
     *
     * @param <T> type of optional as created by the provider
     * @param provider a provider or null (if it doesn't exist)
     * @return the result of the create() option if provider is non-NULL, otherwise {@link
     *     Optional.empty()}
     * @throws CreateException
     */
    public static <T> Optional<T> create(Provider<T> provider) throws CreateException {
        if (provider != null) {
            return Optional.of(provider.create());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Creates only if a boolean flag is TRUE, otherwise returns empty
     *
     * @param <T> type of optional
     * @param toggle boolean flag
     * @param createFunc a function to create a value T, only used if the boolean flag is TRUE
     * @return a present or empty optional depending on the flag
     */
    public static <T> Optional<T> create(boolean toggle, Supplier<T> createFunc) {
        if (toggle) {
            return Optional.of(createFunc.get());
        } else {
            return Optional.empty();
        }
    }
}
