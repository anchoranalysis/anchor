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

package org.anchoranalysis.feature.list;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeatureListStoreUtilities {

    public static void addFeatureListToStoreNoDuplicateDirectly(
            NamedProvider<FeatureList<FeatureInput>> featureListProvider, SharedFeatureMulti out) {
        for (String key : featureListProvider.keys()) {
            try {
                out.addNoDuplicate(featureListProvider.getException(key));
            } catch (NamedProviderGetException e) {
                assert false;
            }
        }
    }

    /**
     * Adds a feature-list to the store
     *
     * @param <T> feature-type
     * @param featureList feature-list
     * @param nameParent the parent-name of the list
     * @param paramsOnlyInDescription iff TRUE, only describe the parameters of the features, but
     *     not the name. Otherwise both are described.
     * @param store store features are added to
     */
    public static <T extends FeatureInput> void addFeatureListToStore(
            FeatureList<T> featureList,
            String nameParent,
            boolean paramsOnlyInDescription,
            NamedFeatureStore<T> store) {

        // We loop over all features in the ni, and call them all the same thing with a number
        for (Feature<T> features : featureList) {

            String chosenName =
                    determineFeatureName(
                            features, nameParent, featureList.size() == 1, paramsOnlyInDescription);

            // We duplicate so that when run in parallel each thread has its own local state for
            // each feature
            //  and uses seperate cached calculation lists
            store.add(chosenName, features.duplicateBean());
        }
    }

    /**
     * Names in the store take the form nameParent.featureDescription unless useOnlyParentName is
     * TRUE, in which case they are called simply nameParent
     */
    private static <T extends FeatureInput> String determineFeatureName(
            Feature<T> feature,
            String nameParent,
            boolean useOnlyParentName,
            boolean paramsOnlyInDescription) {
        if (useOnlyParentName) {
            return nameParent;
        } else {
            return String.format(
                    "%s.%s", nameParent, featureDescription(feature, paramsOnlyInDescription));
        }
    }

    private static String featureDescription(Feature<?> feature, boolean paramsOnlyInDescription) {
        
        // If there's a custom-name on the feature this always takes precedence over the description
        if (!feature.getCustomName().isEmpty()) {
            return feature.getCustomName();
        }
        
        if (paramsOnlyInDescription) {
            return feature.getParamDscr();
        } else {
            return feature.getFriendlyName();
        }
    }
}
