/* (C)2020 */
package org.anchoranalysis.feature.bean.list;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.SkipInit;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

public class FeatureListProviderDefine<T extends FeatureInput>
        extends FeatureListProviderReferencedFeatures<T> {

    // START BEAN PROPERTIES
    @BeanField @SkipInit private List<Feature<T>> list;
    // END BEAN PROPERTIES

    public FeatureListProviderDefine() {
        // DEFAULT, nothing to do
    }

    public FeatureListProviderDefine(Feature<T> feature) {
        this(Arrays.asList(feature));
    }

    public FeatureListProviderDefine(List<Feature<T>> list) {
        this.list = list;
    }

    @Override
    public FeatureList<T> create() throws CreateException {
        return FeatureListFactory.wrapDuplicate(list);
    }

    public List<Feature<T>> getList() {
        return list;
    }

    public void setList(List<Feature<T>> list) {
        this.list = list;
    }
}
