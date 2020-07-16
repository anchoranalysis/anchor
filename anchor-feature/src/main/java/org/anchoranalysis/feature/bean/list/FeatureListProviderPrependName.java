/* (C)2020 */
package org.anchoranalysis.feature.bean.list;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Prepends a string to each feature in the list
 *
 * @author Owen Feehan
 */
public class FeatureListProviderPrependName extends FeatureListProvider<FeatureInput> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private FeatureListProvider<FeatureInput> item;

    @BeanField @Getter @Setter private String prependString;
    // END BEAN PROPERTIES

    public static void setNewNameOnFeature(
            Feature<? extends FeatureInput> f, String existingName, String prependString) {
        f.setCustomName(String.format("%s%s", prependString, existingName));
    }

    @Override
    public FeatureList<FeatureInput> create() throws CreateException {

        FeatureList<FeatureInput> features = item.create();

        for (Feature<FeatureInput> f : features) {
            String existingName = f.getFriendlyName();
            setNewNameOnFeature(f, existingName, prependString);
        }

        return features;
    }
}
