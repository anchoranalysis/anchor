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
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.value.Dictionary;

/**
 * References a particular {@link Dictionary} in shared-object-space.
 *
 * <p>The {@link SharedObjects}-space is provided during initialization.
 *
 * @author Owen Feehan
 */
public class DictionaryProviderReference extends DictionaryProvider {

    // START BEAN PROPERTIES
    /** Unique identifier for the {@link Dictionary} to retrieve. */
    @BeanField @Getter @Setter private String id = "";

    // END BEAN PROPERTIES

    /** The dictionary, assigned during initialization. */
    private Dictionary dictionary;

    @Override
    public void onInitialization(DictionaryInitialization initialization)
            throws InitializeException {
        super.onInitialization(initialization);
        try {
            dictionary = initialization.getDictionaries().getException(id);
        } catch (NamedProviderGetException e) {
            throw new InitializeException(e.summarize());
        }
    }

    @Override
    public Dictionary get() {
        return dictionary;
    }
}
