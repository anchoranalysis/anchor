/*-
 * #%L
 * anchor-feature
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
/* (C)2020 */
package org.anchoranalysis.feature.bean.list;

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
    @BeanField private String collectionID = "";
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

    public String getCollectionID() {
        return collectionID;
    }

    public void setCollectionID(String collectionID) {
        this.collectionID = collectionID;
    }
}
