/*-
 * #%L
 * anchor-feature-session
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

package org.anchoranalysis.feature.session.cache;

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
