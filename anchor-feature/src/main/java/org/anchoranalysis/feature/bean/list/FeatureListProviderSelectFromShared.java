/* (C)2020 */
package org.anchoranalysis.feature.bean.list;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.shared.regex.RegEx;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Selects a number of features of shared-features matching against a regular-expression
 *
 * @author Owen Feehan
 */
public class FeatureListProviderSelectFromShared<T extends FeatureInput>
        extends FeatureListProviderReferencedFeatures<T> {

    // START BEAN PROPERTIES
    @BeanField @OptionalBean @Getter @Setter private RegEx match;
    // END BEAN PROPERTIES

    @Override
    public FeatureList<T> create() throws CreateException {
        try {
            return FeatureListFactory.mapFromFiltered(
                    getInitializationParameters().getFeatureListSet().keys(),
                    key -> match == null || match.hasMatch(key),
                    key ->
                            getInitializationParameters()
                                    .getSharedFeatureSet()
                                    .getException(key)
                                    .downcast());
        } catch (NamedProviderGetException e) {
            throw new CreateException(e);
        }
    }
}
