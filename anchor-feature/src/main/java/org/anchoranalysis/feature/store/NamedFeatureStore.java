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

package org.anchoranalysis.feature.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.name.FeatureNameList;

/**
 * Stores {@link Feature}s, each with an associated name, with list-like access and map-like access.
 *
 * <p>Each feature is stored in an ordered manner, and zero-indexed.
 *
 * <p>A map between the name of each feature, and the feature's index position is simultaneously
 * maintained.
 *
 * <p>If the names of two or more {@link Feature}s are identical, only a single {@link Feature} will
 * be retrieved by name. The list-access remains unaffected.
 *
 * @author Owen Feehan
 * @param <T> the feature-input type for all features in the store.
 */
public class NamedFeatureStore<T extends FeatureInput> implements Iterable<NamedBean<Feature<T>>> {

    private List<NamedBean<Feature<T>>> list = new ArrayList<>();
    private Map<String, Integer> mapIndex = new HashMap<>();

    /**
     * Adds a named-feature to the store. The customName() of the feature is replaced with the name.
     *
     * @param name name of the feature.
     * @param feature the feature to add (whose customName will be overridden with the name).
     */
    public void add(String name, Feature<T> feature) {
        mapIndex.put(name, list.size());
        feature.setCustomName(name);
        list.add(new NamedBean<>(name, feature));
    }

    /**
     * Gets a feature at a particular position.
     *
     * @param index the position to retrieve (zero-indexed).
     * @return the feature, encapsulated in the {@link NamedBean} that contains it.
     */
    public NamedBean<Feature<T>> get(int index) {
        return list.get(index);
    }

    /**
     * Gets a feature corresponding to a particular name.
     *
     * @param name the name of the feature.
     * @return the feature, encapsulated in the {@link NamedBean} that contains it.
     */
    public NamedBean<Feature<T>> get(String name) {
        int index = mapIndex.get(name);
        return list.get(index);
    }

    /**
     * All {@link Feature}s in the store, in identical order.
     *
     * @return a newly-created list, that reuses the existing {@link Feature} instances.
     */
    public FeatureList<T> features() {
        return FeatureListFactory.mapFrom(list, NamedBean::getValue);
    }

    /**
     * The names of all {@link Feature}s in the store, in identical order to the store.
     *
     * @return a newly created {@link FeatureNameList} corresponding to the names of the features.
     */
    public FeatureNameList featureNames() {
        return new FeatureNameList(list.stream().map(NamedBean::getName));
    }

    /**
     * The total number of {@link Feature}s in the store.
     *
     * @return the total number.
     */
    public int size() {
        return list.size();
    }

    @Override
    public Iterator<NamedBean<Feature<T>>> iterator() {
        return list.iterator();
    }

    /**
     * Deep-copies the store, including duplicating each feature.
     *
     * @return a deep-copy of the current instance.
     */
    public NamedFeatureStore<T> duplicate() {
        NamedFeatureStore<T> out = new NamedFeatureStore<>();
        for (NamedBean<Feature<T>> namedBean : list) {
            out.add(namedBean.getName(), namedBean.getValue().duplicateBean());
        }
        return out;
    }
}
