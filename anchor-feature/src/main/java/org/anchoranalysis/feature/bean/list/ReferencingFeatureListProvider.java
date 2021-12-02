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

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.primitive.StringSet;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.shared.FeaturesInitialization;

/**
 * A base class for implementations of {@link FeatureListProvider} that may reference features
 * created elsewhere.
 *
 * @author Owen Feehan
 * @param <T> feature input-type
 */
public abstract class ReferencingFeatureListProvider<T extends FeatureInput>
        extends FeatureListProvider<T> {

    // START BEAN PROPERTIES
    /**
     * The names of other feature-lists, whose features may be referenced by this feature.
     *
     * <p>This ensures these feature-lists are evaluated, before this features in this list are
     * created.
     */
    @BeanField @OptionalBean @Getter @Setter private StringSet references;
    // END BEAN PROPERITES

    @Override
    public void onInitialization(FeaturesInitialization soFeature) throws InitializeException {
        super.onInitialization(soFeature);
        ensureReferencedFeaturesCalled(soFeature);
    }

    private void ensureReferencedFeaturesCalled(FeaturesInitialization so)
            throws InitializeException {
        if (references != null && so != null) {
            for (String featureListReference : references.set()) {

                try {
                    so.getFeatureLists().getException(featureListReference);
                } catch (NamedProviderGetException e) {
                    throw new InitializeException(e.summarize());
                }
            }
        }
    }
}
