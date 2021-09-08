/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.core.identifier.provider;

import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.functional.checked.CheckedFunction;

/**
 * Exposes elements in a {@link NamedProvider} as a different type.
 *
 * @author Owen Feehan
 * @param <S> source-type that is converted <i>from</i>.
 * @param <T> destination-type that is converted <i>to</i>.
 */
@RequiredArgsConstructor
public class NamedProviderBridge<S, T> implements NamedProvider<T> {

    /** The {@link NamedProvider} that supplies elements, before conversion. */
    private final NamedProvider<S> provider;

    /** A function that converts the elements in {@code provider} as they are accessed. */
    private final CheckedFunction<S, T, ? extends Exception> bridge;

    /**
     * Whether to apply the {@code bridge} function to null values.
     *
     * <p>Iff true, the {@code bridge} function is applied to null values. If false, a {@link
     * Optional#empty} is returned instead.
     */
    private final boolean bridgeNulls;

    /**
     * Creates with a particular provider and bridge.
     *
     * @param provider the {@link NamedProvider} that supplies elements, before conversion.
     * @param bridge a function that converts the elements in {@code provider} as they are accessed.
     */
    public NamedProviderBridge(
            NamedProvider<S> provider, CheckedFunction<S, T, ? extends Exception> bridge) {
        this(provider, bridge, true);
    }

    @Override
    public Optional<T> getOptional(String key) throws NamedProviderGetException {
        Optional<S> sourceValue = provider.getOptional(key);

        if (!bridgeNulls && !sourceValue.isPresent()) {
            // Early exit if doNotBridgeNulls is switched on
            return Optional.empty();
        }

        try {
            return OptionalUtilities.map(sourceValue, bridge::apply);
        } catch (Exception e) {
            throw new NamedProviderGetException(key, e);
        }
    }

    @Override
    public Set<String> keys() {
        return provider.keys();
    }
}
