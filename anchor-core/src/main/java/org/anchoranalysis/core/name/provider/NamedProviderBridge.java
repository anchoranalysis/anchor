/* (C)2020 */
package org.anchoranalysis.core.name.provider;

import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.functional.function.FunctionWithException;

/**
 * @author Owen Feehan
 * @param <S> src-type
 * @param <T> destination-type
 */
@RequiredArgsConstructor
public class NamedProviderBridge<S, T> implements NamedProvider<T> {

    private final NamedProvider<S> srcProvider;
    private final FunctionWithException<S, T, ? extends Exception> bridge;
    private final boolean bridgeNulls;

    public NamedProviderBridge(
            NamedProvider<S> srcProvider, FunctionWithException<S, T, ? extends Exception> bridge) {
        this(srcProvider, bridge, true);
    }

    @Override
    public Optional<T> getOptional(String key) throws NamedProviderGetException {
        Optional<S> srcVal = srcProvider.getOptional(key);

        if (!bridgeNulls && !srcVal.isPresent()) {
            // Early exit if doNotBridgeNulls is witched on
            return Optional.empty();
        }

        try {
            return OptionalUtilities.map(srcVal, bridge::apply);
        } catch (Exception e) {
            throw NamedProviderGetException.wrap(key, e);
        }
    }

    @Override
    public Set<String> keys() {
        return srcProvider.keys();
    }
}
