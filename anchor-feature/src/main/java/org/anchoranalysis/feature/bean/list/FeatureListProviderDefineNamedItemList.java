/* (C)2020 */
package org.anchoranalysis.feature.bean.list;

import java.util.List;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.SkipInit;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.list.NamedFeatureStoreFactory;

public class FeatureListProviderDefineNamedItemList<T extends FeatureInput>
        extends FeatureListProviderReferencedFeatures<T> {

    private static final NamedFeatureStoreFactory STORE_FACTORY =
            NamedFeatureStoreFactory.bothNameAndParams();

    // START BEAN PROPERTIES
    @BeanField @SkipInit private List<NamedBean<FeatureListProvider<T>>> list;
    // END BEAN PROPERTIES

    @Override
    public FeatureList<T> create() throws CreateException {

        return FeatureListFactory.mapFrom(
                STORE_FACTORY.createNamedFeatureList(list),
                ni -> renameFeature(ni.getName(), ni.getValue()));
    }

    public List<NamedBean<FeatureListProvider<T>>> getList() {
        return list;
    }

    public void setList(List<NamedBean<FeatureListProvider<T>>> list) {
        this.list = list;
    }

    private static <T extends FeatureInput> Feature<T> renameFeature(
            String name, Feature<T> feature) {
        feature.setCustomName(name);
        return feature;
    }
}
