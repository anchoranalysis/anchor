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

package org.anchoranalysis.feature.shared;

import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.identifier.name.NameValue;
import org.anchoranalysis.core.identifier.provider.NameValueSet;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calculate.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;

public class SharedFeatureSet<T extends FeatureInput> {

    private NameValueSet<Feature<T>> set;

    public SharedFeatureSet(NameValueSet<Feature<T>> set) {
        super();
        this.set = set;
    }

    public void initRecursive(FeatureInitParams featureInitParams, Logger logger)
            throws InitException {
        for (NameValue<Feature<T>> nv : set) {
            nv.getValue().initRecursive(featureInitParams, logger);
        }
    }

    public NameValueSet<Feature<T>> getSet() {
        return set;
    }

    public Feature<T> getException(String name) throws NamedProviderGetException {
        return set.getException(name);
    }
}
