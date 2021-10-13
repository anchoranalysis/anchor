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

package org.anchoranalysis.bean.shared.dictionary;

import lombok.Getter;
import org.anchoranalysis.bean.initializable.params.BeanInitialization;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.value.Dictionary;

/**
 * Initialization parameters that provide a named-mapping to {@link Dictionary}s.
 *
 * @author Owen Feehan
 */
public class DictionaryInitialization implements BeanInitialization {

    /** Named-store of {@link Dictionary} objects. */
    @Getter private final NamedProviderStore<Dictionary> dictionaries;

    /**
     * Creates using a {@link SharedObjects} to create a {@link NamedProviderStore} for the
     * dictionaries.
     *
     * @param sharedObjects the shared-objects.
     */
    public DictionaryInitialization(SharedObjects sharedObjects) {
        dictionaries = sharedObjects.getOrCreate(Dictionary.class);
    }
}
