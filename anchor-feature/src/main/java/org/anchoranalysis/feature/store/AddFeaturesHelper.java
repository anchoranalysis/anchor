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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.input.FeatureInput;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class AddFeaturesHelper {

    /**
     * Adds a feature-list to the store
     *
     * @param <T> feature-type
     * @param featureList feature-list
     * @param nameParent the parent-name of the list
     * @param parametersOnlyInDescription iff true, only describe the parameters of the features,
     *     but not the name. Otherwise both are described.
     * @param store store features are added to
     */
    public static <T extends FeatureInput> void addFeaturesToStore(
            FeatureList<T> featureList,
            String nameParent,
            boolean parametersOnlyInDescription,
            NamedFeatureStore<T> store) {

        // We loop over all features in the ni, and call them all the same thing with a number
        for (Feature<T> features : featureList) {

            String chosenName =
                    determineFeatureName(
                            features,
                            nameParent,
                            featureList.size() == 1,
                            parametersOnlyInDescription);

            // We duplicate so that when run in parallel each thread has its own local state
            // for each feature and uses separate cached calculation lists.
            store.add(chosenName, features.duplicateBean());
        }
    }

    /**
     * Names in the store take the form nameParent.featureDescription unless useOnlyParentName is
     * true, in which case they are called simply nameParent
     */
    private static <T extends FeatureInput> String determineFeatureName(
            Feature<T> feature,
            String nameParent,
            boolean useOnlyParentName,
            boolean parametersOnlyInDescription) {
        if (useOnlyParentName) {
            return nameParent;
        } else {
            return String.format(
                    "%s.%s", nameParent, featureDescription(feature, parametersOnlyInDescription));
        }
    }

    private static String featureDescription(
            Feature<?> feature, boolean parametersOnlyInDescription) {

        // If there's a custom-name on the feature this always takes precedence over the description
        if (!feature.getCustomName().isEmpty()) {
            return feature.getCustomName();
        }

        if (parametersOnlyInDescription) {
            return feature.describeParameters();
        } else {
            return feature.getFriendlyName();
        }
    }
}
