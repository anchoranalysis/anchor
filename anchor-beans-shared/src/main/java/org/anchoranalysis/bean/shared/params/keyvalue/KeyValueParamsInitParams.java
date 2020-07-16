/* (C)2020 */
package org.anchoranalysis.bean.shared.params.keyvalue;

import java.nio.file.Path;
import org.anchoranalysis.bean.init.params.BeanInitParams;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.core.params.KeyValueParams;

public class KeyValueParamsInitParams implements BeanInitParams {

    // START: Stores
    private NamedProviderStore<KeyValueParams> storeKeyValueParams;

    private NamedProviderStore<Path> storeNamedFilePathCollection;
    // END: Stores

    private KeyValueParamsInitParams(SharedObjects so) {
        super();
        storeKeyValueParams = so.getOrCreate(KeyValueParams.class);

        // TODO We are using Strings in general as the key. Might be an idea to be more specific
        storeNamedFilePathCollection = so.getOrCreate(String.class);
    }

    public static KeyValueParamsInitParams create(SharedObjects so) {
        return new KeyValueParamsInitParams(so);
    }

    public NamedProviderStore<KeyValueParams> getNamedKeyValueParamsCollection() {
        return storeKeyValueParams;
    }

    public NamedProviderStore<Path> getNamedFilePathCollection() {
        return storeNamedFilePathCollection;
    }
}
