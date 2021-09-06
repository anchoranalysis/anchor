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
import org.anchoranalysis.bean.xml.exception.ProvisionFailedException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Prepends a string to each feature in the list
 *
 * @author Owen Feehan
 */
public class PrependName extends FeatureListProvider<FeatureInput> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private FeatureListProvider<FeatureInput> item;

    @BeanField @Getter @Setter private String prependString;
    // END BEAN PROPERTIES

    public static void setNewNameOnFeature(
            Feature<? extends FeatureInput> f, String existingName, String prependString) {
        f.setCustomName(String.format("%s%s", prependString, existingName));
    }

    @Override
    public FeatureList<FeatureInput> get() throws ProvisionFailedException {

        FeatureList<FeatureInput> features = item.get();

        for (Feature<FeatureInput> f : features) {
            String existingName = f.getFriendlyName();
            setNewNameOnFeature(f, existingName, prependString);
        }

        return features;
    }
}
