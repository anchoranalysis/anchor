/* (C)2020 */
package org.anchoranalysis.feature.bean.list;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.SkipInit;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

public class FeatureListProviderDefineSingle<T extends FeatureInput>
        extends FeatureListProviderReferencedFeatures<T> {

    /** */

    // START BEAN PROPERTIES
    @BeanField @SkipInit private Feature<T> item;
    // END BEAN PROPERTIES

    @Override
    public FeatureList<T> create() throws CreateException {
        return FeatureListFactory.from(item);
    }

    public Feature<T> getItem() {
        return item;
    }

    public void setItem(Feature<T> item) {
        this.item = item;
    }
}
