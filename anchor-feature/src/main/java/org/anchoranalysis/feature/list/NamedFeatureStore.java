/* (C)2020 */
package org.anchoranalysis.feature.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.name.FeatureNameList;

public class NamedFeatureStore<T extends FeatureInput> implements Iterable<NamedBean<Feature<T>>> {

    private List<NamedBean<Feature<T>>> list = new ArrayList<>();
    private Map<String, Integer> mapIndex = new HashMap<>();

    //

    /**
     * Adds a named-feature to the store. The customName() of the feature is replaced with the name.
     *
     * @param name name of the feature
     * @param feature the feature to add (whose customName will be overridden with the name)
     */
    public void add(String name, Feature<T> feature) {
        mapIndex.put(name, list.size());
        feature.setCustomName(name);
        list.add(new NamedBean<Feature<T>>(name, feature));
    }

    public int getIndex(String name) throws GetOperationFailedException {
        Integer index = mapIndex.get(name);
        if (index == null) {
            throw new GetOperationFailedException(
                    String.format("The key '%s' is not found in the featureStore", name));
        }
        return index;
    }

    public NamedBean<Feature<T>> get(int index) {
        return list.get(index);
    }

    public FeatureNameList createFeatureNames() {
        return new FeatureNameList(list.stream().map(NamedBean::getName));
    }

    public NamedBean<Feature<T>> get(String name) {
        int index = mapIndex.get(name);
        return list.get(index);
    }

    @Override
    public Iterator<NamedBean<Feature<T>>> iterator() {
        return list.iterator();
    }

    public NamedFeatureStore<T> deepCopy() {
        NamedFeatureStore<T> out = new NamedFeatureStore<>();
        for (NamedBean<Feature<T>> ni : list) {
            out.add(ni.getName(), ni.getValue().duplicateBean());
        }
        return out;
    }

    public FeatureList<T> listFeatures() {
        return FeatureListFactory.mapFrom(list, NamedBean::getValue);
    }

    public FeatureList<T> listFeaturesSubset(int start, int size) {
        return FeatureListFactory.mapFromRange(
                start, start + size, index -> list.get(index).getValue());
    }

    public void copyTo(NameValueSet<Feature<T>> out) {
        for (NamedBean<Feature<T>> ni : list) {
            out.add(ni.getName(), ni.getValue());
        }
    }

    public void copyToDuplicate(NameValueSet<Feature<T>> out) {
        for (NamedBean<Feature<T>> ni : list) {
            out.add(ni.getName(), ni.getValue().duplicateBean());
        }
    }

    public int size() {
        return list.size();
    }
}
