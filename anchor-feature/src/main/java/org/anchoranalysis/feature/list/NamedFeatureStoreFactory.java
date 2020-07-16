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

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.error.BeanDuplicateException;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.input.FeatureInput;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NamedFeatureStoreFactory {

    /**
     * iff TRUE, only describe the parameters of the features, but not the name. Otherwise both are
     * described.
     */
    private boolean paramsOnlyInDescription = false;

    public static NamedFeatureStoreFactory factoryParamsOnly() {
        NamedFeatureStoreFactory out = new NamedFeatureStoreFactory();
        out.paramsOnlyInDescription = true;
        return out;
    }

    public static NamedFeatureStoreFactory bothNameAndParams() {
        NamedFeatureStoreFactory out = new NamedFeatureStoreFactory();
        out.paramsOnlyInDescription = false;
        return out;
    }

    /**
     * Create a list of na
     *
     * @param <T>
     * @param listFeatureListProvider
     * @return
     * @throws CreateException
     */
    public <T extends FeatureInput> NamedFeatureStore<T> createNamedFeatureList(
            List<NamedBean<FeatureListProvider<T>>> listFeatureListProvider)
            throws CreateException {

        NamedFeatureStore<T> out = new NamedFeatureStore<>();
        for (NamedBean<FeatureListProvider<T>> ni : listFeatureListProvider) {

            try {
                // NOTE: Naming convention
                //  When a featureList contains a single item, we use the name of the featureList,
                // rather than the feature
                FeatureList<T> featureList = ni.getValue().create();

                if (featureList.size() == 0) {
                    continue;
                }

                FeatureListStoreUtilities.addFeatureListToStore(
                        featureList, ni.getName(), paramsOnlyInDescription, out);

            } catch (BeanDuplicateException | CreateException e) {
                throw new CreateException(
                        String.format(
                                "An error occurred creating a named-feature-list from provider '%s'",
                                ni.getName()),
                        e);
            }
        }
        return out;
    }
}
