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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.functional.FunctionalIterate;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;

/**
 * A list of Strings representing feature names
 *
 * @author Owen Feehan
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FeatureNameList implements Iterable<String> {

    private List<String> delegate;

    public FeatureNameList() {
        delegate = new ArrayList<>();
    }

    public FeatureNameList(Stream<String> stream) {
        delegate = stream.collect(Collectors.toList());
    }

    public FeatureNameList(String firstValue) {
        this();
        delegate.add(firstValue);
    }

    // We wrap an existing list
    public FeatureNameList(Set<String> set) {
        delegate = new ArrayList<>(set);
    }

    public List<String> asList() {
        return delegate;
    }

    /**
     * Creates a map from the feature-names to their indices in the list
     *
     * @return
     */
    public FeatureNameMapToIndex createMapToIndex() {
        FeatureNameMapToIndex out = new FeatureNameMapToIndex();
        for (int i = 0; i < delegate.size(); i++) {
            out.add(delegate.get(i), i);
        }
        return out;
    }

    public FeatureNameList shallowCopy() {
        return new FeatureNameList(delegate.stream());
    }

    public FeatureNameList createUniqueNamesSorted() {
        return new FeatureNameList(delegate.stream().distinct().sorted());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String s : delegate) {
            sb.append(s);
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    /**
     * Adds a feature-name
     *
     * @param name
     */
    public void add(String name) {
        delegate.add(name);
    }

    /**
     * Add the customNames of a feature
     *
     * @param list
     */
    public void addCustomNames(FeatureList<?> list) {
        for (Feature<?> f : list) {
            delegate.add(f.getCustomName());
        }
    }

    /**
     * Add the customNames of a feature with a Prefix
     *
     * @param list
     */
    public void addCustomNamesWithPrefix(String prefix, FeatureList<?> list) {
        for (Feature<?> f : list) {
            delegate.add(prefix + f.getCustomName());
        }
    }

    /**
     * Inserts a new feature-name at the beginning of the list
     *
     * @param name feature-name
     */
    public void insertBeginning(String name) {
        delegate.add(0, name);
    }

    /**
     * Inserts new feature-names at the beginning of the list
     *
     * @param names the feature-names to insert
     */
    public void insertBeginning(String[] names) {
        FunctionalIterate.reverseIterateArray(names, this::insertBeginning);
    }

    @Override
    public Iterator<String> iterator() {
        return delegate.iterator();
    }

    public String get(int index) {
        return delegate.get(index);
    }

    public int size() {
        return delegate.size();
    }
}
