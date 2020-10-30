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

package org.anchoranalysis.feature.bean.list;

import java.util.List;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.SkipInit;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.store.NamedFeatureStoreFactory;
import lombok.Getter;
import lombok.Setter;

public class DefinedNamed<T extends FeatureInput> extends ReferencedFeatures<T> {

    private static final NamedFeatureStoreFactory STORE_FACTORY =
            NamedFeatureStoreFactory.bothNameAndParams();

    // START BEAN PROPERTIES
    @BeanField @SkipInit @Getter @Setter private List<NamedBean<FeatureListProvider<T>>> list;
    // END BEAN PROPERTIES

    @Override
    public FeatureList<T> create() throws CreateException {
        return FeatureListFactory.mapFrom(
                STORE_FACTORY.createNamedFeatureList(list),
                item -> renameFeature(item.getName(), item.getValue()));
    }

    private static <T extends FeatureInput> Feature<T> renameFeature(
            String name, Feature<T> feature) {
        feature.setCustomName(name);
        return feature;
    }
}
