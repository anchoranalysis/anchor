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

import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.identifier.name.NameValue;
import org.anchoranalysis.core.identifier.provider.NameValueMap;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calculate.FeatureInitialization;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * A subset of features from {@link SharedFeatures} that share a common feature input-type.
 *
 * @author Owen Feehan
 * @param <T> the feature input-type
 */
@AllArgsConstructor
public class SharedFeaturesSubset<T extends FeatureInput> {

    /** A map from the name of a feature to the feature-instance. */
    private NameValueMap<Feature<T>> map;

    /**
     * Initialize all features in this instance, recursively also initializating any child-features.
     *
     * @param initialization the parameters for initialization.
     * @param logger a logger that becomes associated with each {@link Feature} for messages or
     *     errors.
     * @throws InitializeException if any feature cannot be successfully initialized.
     */
    public void initializeRecursive(FeatureInitialization initialization, Logger logger)
            throws InitializeException {
        for (NameValue<Feature<T>> namedFeature : map) {
            namedFeature.getValue().initializeRecursive(initialization, logger);
        }
    }

    /**
     * Iterates over each feature in the instance.
     *
     * @param consumer called on each feature in the instance.
     */
    public void forEach(Consumer<Feature<T>> consumer) {
        map.stream().map(NameValue::getValue).forEach(consumer);
    }

    /**
     * Gets a feature by name, throwing an exception if it doesn't exist.
     *
     * @param name the name of the feature.
     * @return the feature.
     * @throws NamedProviderGetException if no feature with {@code name} exists in this instance.
     */
    public Feature<T> getException(String name) throws NamedProviderGetException {
        return map.getException(name);
    }
}
