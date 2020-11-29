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
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.input.FeatureInput;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NamedFeatureStoreFactory {

    /**
     * iff true, only describe the parameters of the features, but not the name. Otherwise both are
     * described.
     */
    private boolean paramsOnlyInDescription = false;

    public static NamedFeatureStoreFactory factoryParamsOnly() {
        return new NamedFeatureStoreFactory(true);
    }

    public static NamedFeatureStoreFactory bothNameAndParams() {
        return new NamedFeatureStoreFactory(false);
    }

    /**
     * Create a list of na
     *
     * @param <T>
     * @param namedFeatures
     * @return
     * @throws CreateException
     */
    public <T extends FeatureInput> NamedFeatureStore<T> createNamedFeatureList(
            List<NamedBean<FeatureListProvider<T>>> namedFeatures) throws CreateException {

        NamedFeatureStore<T> out = new NamedFeatureStore<>();
        for (NamedBean<FeatureListProvider<T>> namedProvider : namedFeatures) {

            try {
                // NOTE: Naming convention
                //  When a featureList contains a single item, we use the name of the featureList,
                // rather than the feature
                FeatureList<T> featureList = namedProvider.getValue().create();

                if (featureList.size() > 0) {
                    AddFeaturesHelper.addFeaturesToStore(
                            featureList, namedProvider.getName(), paramsOnlyInDescription, out);
                }

            } catch (BeanDuplicateException | CreateException e) {
                throw new CreateException(
                        String.format(
                                "An error occurred creating a named-feature-list from provider '%s'",
                                namedProvider.getName()),
                        e);
            }
        }
        return out;
    }
}
