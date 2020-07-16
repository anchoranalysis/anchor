/* (C)2020 */
package org.anchoranalysis.image.bean;

import java.util.List;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Registers beans that needs particular factories
 *
 * @author Owen Feehan
 */
public class RegisterBeanFactoriesImage {

    private RegisterBeanFactoriesImage() {}

    public static void registerBeanFactories() {
        RegisterBeanFactories.register(
                "featureList",
                (List<Feature<FeatureInput>> list) -> FeatureListFactory.wrapReuse(list) // NOSONAR
                );
    }
}
