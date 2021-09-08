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

/**
 * Builds a mapping from names to values, given {@link NameValue} instances.
 *
 * <p>Each name in {@link NameValue} should be unique.
 *
 * <p>This is similar as a standard {@link Map} but additionally:
 *
 * <ul>
 *   <li>iteration provides {@link NameValue} elements.
 *   <li>it exposes itself as a {@link NamedProvider}.
 * </ul>
 *
 * @author Owen Feehan
 * @param <T> element-type in {@link NameValue} instances.
 */
@NoArgsConstructor
public class NameValueMap<T> implements Iterable<NameValue<T>>, NamedProvider<T> {

    private Map<String, NameValue<T>> map = new HashMap<>();

    /**
     * Creates and populates with elements from an {@link Iterable}.
     *
     * @param list the elements to populate with.
     */
    public NameValueMap(Iterable<? extends NameValue<T>> list) {
        for (NameValue<T> namedValue : list) {
            map.put(namedValue.getName(), namedValue);
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

    /**
     * Adds an element.
     *
     * @param name the name that is added.
     * @param value the value that is added.
     */
    public void add(String name, T value) {
        NameValue<T> item = new SimpleNameValue<>(name, value);
        map.put(name, item);
    }

    /**
     * Adds an element.
     *
     * @param value the name and value that is added, reusing the existing object.
     */
    public void add(NameValue<T> value) {
        map.put(value.getName(), value);
    }

    /**
     * Removes an element from the set, if it exists.
     *
     * <p>If the element doesn't exist, nothing happens.
     *
     * @param element the element to remove, if it exists.
     */
    public void removeIfExists(T element) {
        map.remove(element);
    }

    /**
     * The number of elements in the set.
     *
     * @return the number of elements.
     */
    public int size() {
        return map.size();
    }

    /**
     * Exposes the elements in the set as a stream.
     *
     * @return a newly created stream of all elements in the set.
     */
    public Stream<NameValue<T>> stream() {
        return map.values().stream();
    }
}
