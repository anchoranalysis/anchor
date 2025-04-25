/*-
 * #%L
 * anchor-feature
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

package org.anchoranalysis.feature.name;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.functional.FunctionalIterate;

/**
 * A list of {@link String}s representing feature names.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class FeatureNameList implements Iterable<String> {

    private List<String> list;

    /** Create with no names (an empty list). */
    public FeatureNameList() {
        list = new ArrayList<>();
    }

    /**
     * Create from a stream of names.
     *
     * @param stream the stream.
     */
    public FeatureNameList(Stream<String> stream) {
    	// We deliberately wish to have a mutable list to allow later insertion.
    	// See #insertBeginning method
    	list = stream.collect(Collectors.toCollection(java.util.ArrayList::new));
    }

    /**
     * Exposes the underlying list of names.
     *
     * @return the underlying list, which if modified, will also modify this instance.
     */
    public List<String> asList() {
        return list;
    }

    /**
     * Creates a map from the feature-names to their indices in the list.
     *
     * @return a newly created map.
     */
    public FeatureNameMapToIndex createMapToIndex() {
        FeatureNameMapToIndex out = new FeatureNameMapToIndex();
        for (int i = 0; i < list.size(); i++) {
            out.add(list.get(i), i);
        }
        return out;
    }

    /**
     * Shallow copy of the current instance.
     *
     * @return a shallow copy.
     */
    public FeatureNameList duplicateShallow() {
        return new FeatureNameList(list.stream());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s);
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    /**
     * Adds a feature-name.
     *
     * @param name the name to add.
     */
    public void add(String name) {
        list.add(name);
    }

    /**
     * Inserts a new feature-name at the beginning of the list.
     *
     * @param name feature-name to insert.
     */
    public void insertBeginning(String name) {
        list.add(0, name);
    }

    /**
     * Inserts new feature-names at the beginning of the list.
     *
     * @param names the feature-names to insert.
     */
    public void insertBeginning(String[] names) {
        FunctionalIterate.reverseIterateArray(names, this::insertBeginning);
    }

    @Override
    public Iterator<String> iterator() {
        return list.iterator();
    }

    /**
     * Gets a name corresponding to a particular position.
     *
     * @param index the position of the name in the list (zero-indexed).
     * @return the corresponding name.
     */
    public String get(int index) {
        return list.get(index);
    }

    /**
     * The number of names in the list.
     *
     * @return the total number of names.
     */
    public int size() {
        return list.size();
    }
}
