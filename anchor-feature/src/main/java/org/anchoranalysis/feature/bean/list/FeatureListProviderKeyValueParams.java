/* (C)2020 */
package org.anchoranalysis.feature.bean.list;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.operator.Constant;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Loads a set of KeyValueParams as features
 *
 * @author Owen Feehan
 */
public class FeatureListProviderKeyValueParams<T extends FeatureInput>
        extends FeatureListProvider<T> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String collectionID = "";
    // END BEAN PROPERTIES

    @Override
    public FeatureList<T> create() throws CreateException {

        try {
            KeyValueParams kpv =
                    getInitializationParameters()
                            .getParams()
                            .getNamedKeyValueParamsCollection()
                            .getException(collectionID);

            return FeatureListFactory.mapFrom(kpv.keySet(), key -> featureForKey(key, kpv));

        } catch (NamedProviderGetException e) {
            throw new CreateException(e);
        }
    }

    private Feature<T> featureForKey(String key, KeyValueParams kpv) {
        return new Constant<>(key, kpv.getPropertyAsDouble(key));
    }
}
