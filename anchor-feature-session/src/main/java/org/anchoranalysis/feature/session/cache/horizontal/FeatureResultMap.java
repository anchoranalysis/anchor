/* (C)2020 */
package org.anchoranalysis.feature.session.cache.horizontal;

import java.util.HashMap;
import java.util.Map;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

class FeatureResultMap<T extends FeatureInput> {

    private Map<Feature<T>, Double> mapFeature = new HashMap<>();
    private Map<String, Double> mapID = new HashMap<>();
    private Map<String, Feature<T>> mapFeatureName = new HashMap<>();

    /**
     * Adds a feature to map.
     *
     * <p>Any existing entry with the same feature-name is replaced.
     *
     * @param feature feature to add
     */
    public void add(Feature<T> feature, String featureName, Double result) {
        mapFeature.put(feature, result);
        mapID.put(featureName, result);
    }

    /**
     * Adds a feature to map.
     *
     * <p>Any existing entry with the same feature-name is replaced.
     *
     * @param feature feature to add
     */
    public void add(Feature<T> feature) {
        String customName = feature.getCustomName();
        if (customName != null && !customName.isEmpty()) {
            mapFeatureName.put(customName, feature);
        }
    }

    public void clear() {
        mapFeature.clear();
        mapID.clear();
    }

    public Feature<T> getFeatureFor(String name) {
        return mapFeatureName.get(name);
    }

    public Double getResultFor(Feature<T> feature) {
        return mapFeature.get(feature);
    }

    public Double getResultFor(String name) {
        return mapID.get(name);
    }
}
