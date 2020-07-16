/* (C)2020 */
package org.anchoranalysis.bean.shared.params.keyvalue;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.params.KeyValueParams;

public class KeyValueParamsProviderReference extends KeyValueParamsProvider {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String id = "";
    // END BEAN PROPERTIES

    private KeyValueParams params;

    @Override
    public void onInit(KeyValueParamsInitParams so) throws InitException {
        super.onInit(so);
        try {
            params = so.getNamedKeyValueParamsCollection().getException(id);
        } catch (NamedProviderGetException e) {
            throw new InitException(e.summarize());
        }
    }

    @Override
    public KeyValueParams create() {
        assert (getInitializationParameters() != null); // Otherwise init() has never been called
        return params;
    }
}
