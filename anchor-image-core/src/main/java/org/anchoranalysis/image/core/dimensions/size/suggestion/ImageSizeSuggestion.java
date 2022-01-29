/*-
 * #%L
 * anchor-image-core
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
package org.anchoranalysis.image.core.dimensions.size.suggestion;

import java.util.Optional;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * A suggestion on a size for an image.
 *
 * @author Owen Feehan
 */
public interface ImageSizeSuggestion {

    /**
     * Calculates the scaling factor.
     *
     * @param extentToBeScaled dimensions of the source image/entity that will be scaled, if they
     *     are known.
     * @return the scaling-factor to use
     * @throws OperationFailedException if insufficient information is available to calculation a
     *     factor.
     */
    ScaleFactor calculateScaleFactor(Optional<Extent> extentToBeScaled)
            throws OperationFailedException;

    /**
     * A {@link ScaleFactor}, if one exists, that is applied <b>uniformally</b>, independent of the
     * {@link Extent} to be scaled.
     *
     * @return such a scale-factor, if it exists. {@link Optional#empty} if it does not exist.
     */
    Optional<ScaleFactor> uniformScaleFactor();

    /**
     * A specific <b>width</b>, if one exists, to which each image should be resized.
     *
     * @return the width if it exists, otherwise {@link Optional#empty}.
     */
    Optional<Integer> uniformWidth();

    /**
     * A specific <b>height</b>, if one exists, to which each image should be resized.
     *
     * @return the height if it exists, otherwise {@link Optional#empty}.
     */
    Optional<Integer> uniformHeight();
}
