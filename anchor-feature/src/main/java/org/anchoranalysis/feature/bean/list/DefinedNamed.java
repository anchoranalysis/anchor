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
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.SkipInit;
import org.anchoranalysis.bean.xml.exception.ProvisionFailedException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.store.NamedFeatureStoreFactory;

/**
 * Specifies features via a list of names and associated with features.
 *
 * <p>The associated name is assigned to each feature it is associated with.
 *
 * <p>Note that if multiple features are associated with the same name (i.e. multiple entries in the
 * list in {@code NamedBean<FeatureListProvider<T>>} this can result with multiple features with an
 * identical custom-name.
 *
 * @author Owen Feehan
 * @param <T> the feature input-type
 */
public class DefinedNamed<T extends FeatureInput> extends ReferencingFeatureListProvider<T> {

    private static final NamedFeatureStoreFactory STORE_FACTORY =
            NamedFeatureStoreFactory.bothNameAndParameters();

    // START BEAN PROPERTIES
    /** A list of {@link FeatureListProvider}s with an associated name. */
    @BeanField @SkipInit @Getter @Setter private List<NamedBean<FeatureListProvider<T>>> list;
    // END BEAN PROPERTIES

    @Override
    public FeatureList<T> get() throws ProvisionFailedException {
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
