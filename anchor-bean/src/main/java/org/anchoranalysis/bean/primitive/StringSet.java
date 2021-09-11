/*-
 * #%L
 * anchor-bean
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

package org.anchoranalysis.bean.primitive;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.anchoranalysis.bean.AnchorBean;

/**
 * A bean defining a set of {@link String}s.
 *
 * <p>The elements are stored internally in a consistent ordering, according to their natural
 * comparator.
 *
 * <p>An example:
 *
 * <pre>{@code
 *    <datasets config-class="org.anchoranalysis.bean.primitive.StringSet" config-factory="stringSet">
 * 	<item>first_dataset</item>
 * 	<item>second_dataset</item>
 * 	<item>some_other_dataset</item>
 *   </datasets>
 * }</pre>
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true)
public class StringSet extends AnchorBean<StringSet> implements PrimitiveBeanCollection<String> {

    /** The underlying set of values. */
    @Getter private Set<String> set = new TreeSet<>();

    /**
     * Constructs with one or more values added to the list.
     *
     * @param values the values
     */
    public StringSet(String... values) {
        Arrays.stream(values).forEach(set::add);
    }

    @Override
    public void add(String value) {
        set.add(value);
    }

    @Override
    public boolean contains(String value) {
        return set.contains(value);
    }

    @Override
    public Iterator<String> iterator() {
        return set.iterator();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    /**
     * Creates a stream of elements in the set.
     *
     * @return a newly created stream.
     */
    public Stream<String> stream() {
        return set.stream();
    }

    /**
     * Duplicate the bean.
     *
     * <p>NOTE: We need to specifically-implement it as the {@link AnchorBean} functionality won't
     * work with this implementation, as it uses non-default initialization (using a config-factory)
     */
    @Override
    public StringSet duplicateBean() {
        StringSet out = new StringSet();
        out.set.addAll(set);
        return out;
    }
}
