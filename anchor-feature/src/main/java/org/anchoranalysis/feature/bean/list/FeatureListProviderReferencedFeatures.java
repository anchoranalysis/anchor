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
import org.anchoranalysis.bean.StringSet;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;

public abstract class FeatureListProviderReferencedFeatures<T extends FeatureInput>
        extends FeatureListProvider<T> {

    // START BEAN PROPERTIES
    @BeanField @OptionalBean @Getter @Setter
    /**
     * Ensures any feature-lists mentioned here are evaluated, before this list is created.
     *
     * <p>Useful for when this list references another list.
     */
    private StringSet referencesFeatureListCreator;
    // END BEAN PROPERITES

    @Override
    public void onInit(SharedFeaturesInitParams soFeature) throws InitException {
        super.onInit(soFeature);
        ensureReferencedFeaturesCalled(soFeature);
    }

    private void ensureReferencedFeaturesCalled(SharedFeaturesInitParams so) throws InitException {
        if (referencesFeatureListCreator != null && so != null) {
            for (String s : referencesFeatureListCreator.set()) {

                try {
                    so.getFeatureListSet().getException(s);
                } catch (NamedProviderGetException e) {
                    throw new InitException(e.summarize());
                }
            }
        }
    }
}
