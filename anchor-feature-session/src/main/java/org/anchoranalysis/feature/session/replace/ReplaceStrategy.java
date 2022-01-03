/*-
 * #%L
 * anchor-feature-session
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

package org.anchoranalysis.feature.session.replace;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.feature.calculate.FeatureCalculationInput;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Different strategies on associating a {@link FeatureCalculationInput} with a particular {@code
 * input}.
 *
 * <p>A new {@link FeatureCalculationInput} may be created, or an existing one reused - with or
 * without invalidating it.
 *
 * <p>Depending on the relationships that may exist between successive inputs that are calculated,
 * particular caching strategies can be used to avoid repeating redundant calculation. This class
 * provides an abstract interface for one aspect of that relationship.
 *
 * @author Owen
 * @param <T> the feature input-type
 */
public interface ReplaceStrategy<T extends FeatureInput> {

    /**
     * Find or create a {@link FeatureCalculationInput} to associate with {@code input}.
     *
     * @param input the input that a {@link FeatureCalculationInput} will be associated with.
     * @return the {@link FeatureCalculationInput}, either newly created, or reused from a previous
     *     call.
     * @throws OperationFailedException if an error-state emerge
     */
    FeatureCalculationInput<T> createOrReuse(T input) throws OperationFailedException;
}
