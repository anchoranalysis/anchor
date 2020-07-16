/* (C)2020 */
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
