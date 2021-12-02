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

package org.anchoranalysis.feature.input;

import java.util.Optional;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.image.core.dimensions.Resolution;

/**
 * A {@link FeatureInput} that exposes the {@link Resolution} of an image or scene.
 *
 * @author Owen Feehan
 */
public interface FeatureInputWithResolution extends FeatureInput {

    /**
     * The resolution of the associated image, if it is available.
     *
     * @return the resolution, if it is available.
     */
    Optional<Resolution> getResolutionOptional();

    /**
     * The resolution of the associated image, or an exception if it is not available.
     *
     * @return the resolution.
     * @throws FeatureCalculationException if the resolution is not available.
     */
    default Resolution getResolutionRequired() throws FeatureCalculationException {
        return getResolutionOptional()
                .orElseThrow(
                        () ->
                                new FeatureCalculationException(
                                        "An image-resolution is required to be associated with the input for this feature"));
    }
}
