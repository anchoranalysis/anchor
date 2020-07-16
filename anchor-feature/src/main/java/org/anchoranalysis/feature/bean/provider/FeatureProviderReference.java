/* (C)2020 */
package org.anchoranalysis.feature.bean.provider;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

public class FeatureProviderReference extends FeatureProvider<FeatureInput> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String id = "";

    @BeanField @Getter @Setter private String featureListRef = "";
    // END BEAN PROPERTIES

    private Feature<FeatureInput> feature;

    @Override
    public Feature<FeatureInput> create() throws CreateException {
        if (feature == null) {
            if (getInitializationParameters().getSharedFeatureSet() == null) {
                throw new CreateException("sharedFeatureSet is null");
            }

            if (featureListRef != null && !featureListRef.isEmpty()) {
                // We request this to make sure it's evaluated and added to the
                // pso.getSharedFeatureSet()
                try {
                    getInitializationParameters().getFeatureListSet().getException(featureListRef);
                } catch (NamedProviderGetException e) {
                    throw new CreateException(e.summarize());
                }
            }

            try {
                this.feature = getInitializationParameters().getSharedFeatureSet().getException(id);
            } catch (NamedProviderGetException e) {
                throw new CreateException(e.summarize());
            }
        }
        return feature;
    }
}
