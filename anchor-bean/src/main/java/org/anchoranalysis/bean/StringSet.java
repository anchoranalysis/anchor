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

package org.anchoranalysis.bean;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import lombok.NoArgsConstructor;

/**
 * A set of strings.
 *
 * <p>We use TreeSet to maintain an ordering that is consistent and meaningful for the iterator().
 *
 * <p>An example:
 *
 * <pre>{@code
 *    <datasets config-class="org.anchoranalysis.bean.StringSet" config-factory="stringSet">
 * 	<item>first_dataset</item>
 * 	<item>second_dataset</item>
 * 	<item>some_other_dataset</item>
 *   </datasets>
 * }</pre>
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class StringSet extends AnchorBean<StringSet> implements StringBeanCollection {

    private Set<String> set = new TreeSet<>();

    public StringSet(String ...strings) {
        Arrays.stream(strings).forEach(set::add);
    }
    @Override
    public void add(String s) {
        set.add(s);
    }

    @Override
    public boolean contains(String s) {
        return set.contains(s);
    }

    public Set<String> set() {
        return set;
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
     * Duplicate the bean
     *
     * <p>NOTE: We need to specifically-implement it as the GeneralBean functionality won't work
     * with this implementation, as it uses non-default initialization (using a config-factory)
     */
    @Override
    public StringSet duplicateBean() {
        StringSet out = new StringSet();
        out.set.addAll(set);
        return out;
    }
}
