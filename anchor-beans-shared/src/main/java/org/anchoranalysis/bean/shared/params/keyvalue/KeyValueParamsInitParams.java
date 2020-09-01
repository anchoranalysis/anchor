/*-
 * #%L
 * anchor-beans-shared
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.bean.shared.params.keyvalue;

import java.nio.file.Path;
import org.anchoranalysis.bean.initializable.params.BeanInitParams;
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
