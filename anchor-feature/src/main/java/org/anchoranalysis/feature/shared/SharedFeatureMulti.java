/* (C)2020 */
package org.anchoranalysis.feature.shared;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.core.name.value.SimpleNameValue;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.descriptor.FeatureInputType;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

/**
 * A group of features (of possibly heterogeneous type) made available to other features to
 * reference
 *
 * @author Owen Feehan
 */
public class SharedFeatureMulti
        implements NamedProvider<Feature<FeatureInput>>,
                Iterable<NameValue<Feature<FeatureInput>>> {

    /** For searching by key */
    private NameValueSet<Feature<FeatureInput>> mapByKey;

    /**
     * For subsetting by descriptor-type
     *
     * <pre>Key=<FeatureInputDescriptor</pre>
     *
     * <pre>Value=INameValue<Feature<FeatureInput>>></pre>
     */
    private MultiMap mapByDescriptor;

    /** For checking if a feature already exists */
    private Set<Feature<FeatureInput>> setFeatures;

    public SharedFeatureMulti() {
        mapByKey = new NameValueSet<>();
        mapByDescriptor = new MultiValueMap();
        setFeatures = new HashSet<>();
    }

    /** Extracts the subset of inputs that are compatible with a particular input-type */
    @SuppressWarnings("unchecked")
    public <S extends FeatureInput> SharedFeatureSet<S> subsetCompatibleWith(
            Class<? extends FeatureInput> inputType) {

        NameValueSet<Feature<S>> out = new NameValueSet<>();

        for (Class<? extends FeatureInput> descriptor :
                (Set<Class<? extends FeatureInput>>) mapByDescriptor.keySet()) {
            if (FeatureInputType.isCompatibleWith(descriptor, inputType)) {
                transferToSet(
                        (Collection<NameValue<Feature<S>>>) mapByDescriptor.get(descriptor), out);
            }
        }

        return new SharedFeatureSet<>(out);
    }

    @Override
    public String toString() {
        return mapByKey.keys().toString();
    }

    public boolean contains(Feature<FeatureInput> feature) {
        return setFeatures.contains(feature);
    }

    public SharedFeatureMulti duplicate() {
        SharedFeatureMulti out = new SharedFeatureMulti();

        for (NameValue<Feature<FeatureInput>> nv : mapByKey) {
            out.addNoDuplicate(nv);
        }
        return out;
    }

    public void addNoDuplicate(FeatureList<FeatureInput> features) {

        // We loop over all features in the ni, and call them all the same thing with a number
        for (Feature<FeatureInput> f : features) {

            addNoDuplicate(new SimpleNameValue<>(f.getFriendlyName(), f));
        }
    }

    private void addNoDuplicate(NameValue<Feature<FeatureInput>> nv) {
        mapByKey.add(nv);
        mapByDescriptor.put(nv.getValue().inputType(), nv);
        setFeatures.add(nv.getValue());
    }

    public void removeIfExists(FeatureList<FeatureInput> features) {
        // We loop over all features in the ni, and call them all the same thing with a number
        for (Feature<FeatureInput> f : features) {
            mapByKey.removeIfExists(f);
        }
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

    /** Transfers from a collection of name-values into a {@link NameValueSet} */
    private static <S extends FeatureInput> void transferToSet(
            Collection<NameValue<Feature<S>>> in, NameValueSet<Feature<S>> out) {
        for (NameValue<Feature<S>> nv : in) {
            out.add(nv);
        }
    }
}
