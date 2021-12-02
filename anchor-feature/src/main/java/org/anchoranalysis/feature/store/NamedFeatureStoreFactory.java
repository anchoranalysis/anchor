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

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.exception.BeanDuplicateException;
import org.anchoranalysis.bean.xml.exception.ProvisionFailedException;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Creates a {@link NamedFeatureStore}.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NamedFeatureStoreFactory {

    /**
     * Iff true, only describe the parameters of the features, but not the name. Otherwise both are
     * described.
     */
    private boolean parametersOnlyInDescription = false;

    /**
     * The custom-names of the features are derived only from their parameters, but not their name.
     *
     * @return a newly-created factory, that creates features as above.
     */
    public static NamedFeatureStoreFactory parametersOnly() {
        return new NamedFeatureStoreFactory(true);
    }

    /**
     * The custom-names of the features are derived from both their name and their parameters.
     *
     * @return a newly-created factory, that creates features as above.
     */
    public static NamedFeatureStoreFactory bothNameAndParameters() {
        return new NamedFeatureStoreFactory(false);
    }

    /**
     * Create a {@link NamedFeatureStore} from a list of beans.
     *
     * @param <T> feature input-type.
     * @param namedFeatures the beans that provide lists of features.
     * @return a newly created store, with the name of features derived from the beans and the
     *     parameterization of this factory instance.
     * @throws ProvisionFailedException if any feature-list cannot be created from the bean.
     */
    public <T extends FeatureInput> NamedFeatureStore<T> createNamedFeatureList(
            List<NamedBean<FeatureListProvider<T>>> namedFeatures) throws ProvisionFailedException {

        NamedFeatureStore<T> out = new NamedFeatureStore<>();
        for (NamedBean<FeatureListProvider<T>> namedProvider : namedFeatures) {

            try {
                // NOTE: Naming convention
                //  When a featureList contains a single item, we use the name of the featureList,
                // rather than the feature
                FeatureList<T> featureList = namedProvider.getValue().get();

                if (featureList.size() > 0) {
                    AddFeaturesHelper.addFeaturesToStore(
                            featureList, namedProvider.getName(), parametersOnlyInDescription, out);
                }

            } catch (BeanDuplicateException | ProvisionFailedException e) {
                throw new ProvisionFailedException(
                        String.format(
                                "An error occurred creating a named-feature-list from provider '%s'",
                                namedProvider.getName()),
                        e);
            }
        }
        return out;
    }
}
