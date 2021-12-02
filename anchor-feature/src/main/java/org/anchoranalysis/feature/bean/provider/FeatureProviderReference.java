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

package org.anchoranalysis.feature.bean.provider;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.xml.exception.ProvisionFailedException;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.initialization.FeatureRelatedInitialization;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Provides an existing {@link Feature} identified by its name and optionally a feature-list in
 * which is resides.
 *
 * @author Owen Feehan
 */
public class FeatureProviderReference extends FeatureProvider<FeatureInput> {

    // START BEAN PROPERTIES
    /** The name of the feature to reference. */
    @BeanField @Getter @Setter private String id = "";

    /** The name of the list in which the feature referenced by {@code id} resides. */
    @BeanField @Getter @Setter private String referencesList = "";
    // END BEAN PROPERTIES

    // The memoized feature, once it is first retrieved.
    private Feature<FeatureInput> feature;

    @Override
    public Feature<FeatureInput> get() throws ProvisionFailedException {
        try {
            if (feature == null) {
                feature = createFeature(getInitialization());
            }
            return feature;
        } catch (InitializeException e) {
            throw new ProvisionFailedException(e);
        }
    }

    private Feature<FeatureInput> createFeature(FeatureRelatedInitialization initialization)
            throws ProvisionFailedException {
        if (initialization.getSharedFeatures() == null) {
            throw new ProvisionFailedException("shared-features are not defined.");
        }

        if (referencesList != null && !referencesList.isEmpty()) {
            // We request this to make sure it's evaluated and added to the
            // pso.getSharedFeatureSet()
            try {
                initialization.getFeatureLists().getException(referencesList);
            } catch (NamedProviderGetException e) {
                throw new ProvisionFailedException(e.summarize());
            }
        }

        try {
            return initialization.getSharedFeatures().getException(id);
        } catch (NamedProviderGetException e) {
            throw new ProvisionFailedException(e.summarize());
        }
    }
}
