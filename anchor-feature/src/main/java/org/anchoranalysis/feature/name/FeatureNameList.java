/* (C)2020 */
package org.anchoranalysis.feature.name;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;

/**
 * A list of Strings representing feature names
 *
 * @author Owen Feehan
 */
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

    /**
     * Inserts a new feature-name at the beginning of the list
     *
     * @param name feature-name
     */
    public void insertBeginning(String name) {
        delegate.add(0, name);
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
