/* (C)2020 */
package org.anchoranalysis.feature.bean.list;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.StringSet;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;

public abstract class FeatureListProviderReferencedFeatures<T extends FeatureInput>
        extends FeatureListProvider<T> {

    // START BEAN PROPERTIES
    @BeanField @OptionalBean @Getter @Setter
    /**
     * Ensures any feature-lists mentioned here are evaluated, before this list is created.
     *
     * <p>Useful for when this list references another list.
     */
    private StringSet referencesFeatureListCreator;
    // END BEAN PROPERITES

    @Override
    public void onInit(SharedFeaturesInitParams soFeature) throws InitException {
        super.onInit(soFeature);
        ensureReferencedFeaturesCalled(soFeature);
    }

    private void ensureReferencedFeaturesCalled(SharedFeaturesInitParams so) throws InitException {
        if (referencesFeatureListCreator != null && so != null) {
            for (String s : referencesFeatureListCreator.set()) {

                try {
                    so.getFeatureListSet().getException(s);
                } catch (NamedProviderGetException e) {
                    throw new InitException(e.summarize());
                }
            }
        }
    }
}
