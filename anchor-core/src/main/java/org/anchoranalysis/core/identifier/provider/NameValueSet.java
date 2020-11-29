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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.identifier.name.NameValue;
import org.anchoranalysis.core.identifier.name.SimpleNameValue;

@NoArgsConstructor
public class NameValueSet<T> implements Iterable<NameValue<T>>, NamedProvider<T> {

    private Map<String, NameValue<T>> map = new HashMap<>();

    public NameValueSet(Iterable<? extends NameValue<T>> list) {
        super();

        for (NameValue<T> nmp : list) {
            map.put(nmp.getName(), nmp);
        }
    }

    @Override
    public Set<String> keys() {
        return map.keySet();
    }

    @Override
    public Optional<T> getOptional(String key) {
        NameValue<T> item = map.get(key);
        return Optional.ofNullable(item).map(NameValue::getValue);
    }

    @Override
    public Iterator<NameValue<T>> iterator() {
        return map.values().iterator();
    }

    public void add(String name, T value) {
        NameValue<T> item = new SimpleNameValue<>(name, value);
        map.put(name, item);
    }

    public void add(NameValue<T> ni) {
        map.put(ni.getName(), ni);
    }

    public void add(NameValueSet<T> set) {
        for (String key : set.keys()) {
            try {
                add(key, set.getException(key));
            } catch (NamedProviderGetException e) {
                // This should never occur as we always use known key-values
                assert false;
            }
        }
    }

    public void removeIfExists(T item) {
        map.remove(item);
    }

    public int size() {
        return map.size();
    }

    // Maybe this doesn't work too well
    public T getArbitrary() {
        return map.get(map.keySet().iterator().next()).getValue();
    }

    public Stream<NameValue<T>> stream() {
        return map.values().stream();
    }
}
