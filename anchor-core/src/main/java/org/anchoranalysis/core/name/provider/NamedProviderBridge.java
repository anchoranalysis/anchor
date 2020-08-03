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

package org.anchoranalysis.core.name.provider;

import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.functional.function.CheckedFunction;

/**
 * @author Owen Feehan
 * @param <S> src-type
 * @param <T> destination-type
 */
@RequiredArgsConstructor
public class NamedProviderBridge<S, T> implements NamedProvider<T> {

    private final NamedProvider<S> srcProvider;
    private final CheckedFunction<S, T, ? extends Exception> bridge;
    private final boolean bridgeNulls;

    public NamedProviderBridge(
            NamedProvider<S> srcProvider, CheckedFunction<S, T, ? extends Exception> bridge) {
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
