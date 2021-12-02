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

package org.anchoranalysis.feature.shared;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.identifier.name.NameValue;
import org.anchoranalysis.core.identifier.name.SimpleNameValue;
import org.anchoranalysis.core.identifier.provider.NameValueMap;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.FeatureInputType;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

/**
 * A group of features made available to other features to reference.
 *
 * <p>The features may have heterogeneous feature input-type, and are therefore stored with {@link
 * FeatureInput} input-type, as this is the parent type for all feature-inputs.
 *
 * <p>This is the principle class for storing <i>all</i> available shared-features.
 *
 * @author Owen Feehan
 */
public class SharedFeatures
        implements NamedProvider<Feature<FeatureInput>>,
                Iterable<NameValue<Feature<FeatureInput>>> {

    /** For searching by key. */
    private NameValueMap<Feature<FeatureInput>> mapByKey;

    /**
     * For subsetting by descriptor-type.
     *
     * <pre>Key=<FeatureInputDescriptor</pre>
     *
     * <pre>Value=INameValue<Feature<FeatureInput>>></pre>
     */
    private MultiMap mapByDescriptor;

    /** For checking if a feature already exists. */
    private Set<Feature<FeatureInput>> setFeatures;

    /** Create empty, without any features. */
    public SharedFeatures() {
        mapByKey = new NameValueMap<>();
        mapByDescriptor = new MultiValueMap();
        setFeatures = new HashSet<>();
    }

    /**
     * Extracts the subset of inputs that are compatible with a particular input-type.
     *
     * @param inputType the class of input-type which we search for features to be compatible with.
     * @return a new {@link SharedFeaturesSubset} containing only the features considered compatible
     *     with {@code inputType}.
     */
    @SuppressWarnings("unchecked")
    public <S extends FeatureInput> SharedFeaturesSubset<S> subsetCompatibleWith(
            Class<? extends FeatureInput> inputType) {

        NameValueMap<Feature<S>> out = new NameValueMap<>();

        for (Class<? extends FeatureInput> descriptor :
                (Set<Class<? extends FeatureInput>>) mapByDescriptor.keySet()) {
            if (FeatureInputType.isCompatibleWith(descriptor, inputType)) {
                transferToSet(
                        (Collection<NameValue<Feature<S>>>) mapByDescriptor.get(descriptor), out);
            }
        }

        return new SharedFeaturesSubset<>(out);
    }

    @Override
    public String toString() {
        return mapByKey.keys().toString();
    }

    /**
     * Whether a particular feature is contained in this instance.
     *
     * <p>This is checked in constant average-case lookup time.
     *
     * @param feature the feature to check.
     * @return true if the feature is contained, false otherwise.
     */
    public boolean contains(Feature<FeatureInput> feature) {
        return setFeatures.contains(feature);
    }

    /**
     * A deep copy of the current instance.
     *
     * @return a deep copy.
     */
    public SharedFeatures duplicate() {
        SharedFeatures out = new SharedFeatures();

        for (NameValue<Feature<FeatureInput>> nameValue : mapByKey) {
            out.addNoDuplicate(nameValue);
        }
        return out;
    }

    /**
     * Add features from a {@link NamedProvider} of feature-lists.
     *
     * <p>Each {@link Feature} is added directly (without duplication).
     *
     * @param provider the provider to add features from.
     */
    public void addFromProviders(NamedProvider<FeatureList<FeatureInput>> provider) {
        for (String key : provider.keys()) {
            try {
                addFromList(provider.getException(key));
            } catch (NamedProviderGetException e) {
                throw new AnchorImpossibleSituationException();
            }
        }
    }

    /**
     * Add features from a feature-list.
     *
     * <p>Each {@link Feature} is added directly (without duplication).
     *
     * @param list the list to add features from.
     */
    public void addFromList(FeatureList<FeatureInput> list) {

        // We loop over all features in the ni, and call them all the same thing with a number
        for (Feature<FeatureInput> feature : list) {

            addNoDuplicate(new SimpleNameValue<>(feature.getFriendlyName(), feature));
        }
    }

    /**
     * Remove all features, if they currently exist, from {@code features}.
     *
     * @param featuresToRemove a list of features to remove.
     */
    public void removeIfExists(FeatureList<FeatureInput> featuresToRemove) {
        featuresToRemove.forEach(mapByKey::removeIfExists);
    }

    @Override
    public Iterator<NameValue<Feature<FeatureInput>>> iterator() {
        return mapByKey.iterator();
    }

    @Override
    public Optional<Feature<FeatureInput>> getOptional(String key) {
        return mapByKey.getOptional(key);
    }

    @Override
    public Set<String> keys() {
        return mapByKey.keys();
    }

    /** Adds a feature - without duplicating it. */
    private void addNoDuplicate(NameValue<Feature<FeatureInput>> feature) {
        mapByKey.add(feature);
        mapByDescriptor.put(feature.getValue().inputType(), feature);
        setFeatures.add(feature.getValue());
    }

    /** Transfers from a collection of name-values into a {@link NameValueMap}. */
    private static <S extends FeatureInput> void transferToSet(
            Collection<NameValue<Feature<S>>> in, NameValueMap<Feature<S>> out) {
        for (NameValue<Feature<S>> nv : in) {
            out.add(nv);
        }
    }
}
