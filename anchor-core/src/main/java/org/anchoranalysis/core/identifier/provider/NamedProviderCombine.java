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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Combines one more {@link NamedProvider}s into a unitary {@link NamedProvider}.
 *
 * <p>Queries are applied sequentially to each {@link NamedProvider} until successful.
 *
 * <p>All keys from all underlying {@link NamedProvider}s are valid, but maximally one entry will be
 * returned for a corresponding key, as first encountered during iteration.
 *
 * @author Owen Feehan
 * @param <T> element-type
 */
public class NamedProviderCombine<T> implements NamedProvider<T> {

    private final List<NamedProvider<T>> list;

    /**
     * Create from a stream of {@link NamedProvider}s.
     *
     * <p>Note that the order of this stream, determines the order in which queries occur.
     *
     * @param stream the stream
     */
    public NamedProviderCombine(Stream<NamedProvider<T>> stream) {
        this.list = stream.toList();
    }

    @Override
    public Optional<T> getOptional(String key) throws NamedProviderGetException {

        for (NamedProvider<T> item : list) {

            Optional<T> value = item.getOptional(key);

            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<String> keys() {
        return list.stream()
                .flatMap(item -> item.keys().stream())
                .collect(Collectors.toCollection(HashSet::new));
    }
}
