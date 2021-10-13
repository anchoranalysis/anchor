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
package org.anchoranalysis.image.core.dimensions.size;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.scale.ScaleFactor;
import org.anchoranalysis.spatial.scale.Scaler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResizeExtentUtilities {

    /**
     * Multiplexes between {@link #relativeScalePreserveAspectRatio(Extent, Extent)} and {@link
     * #relativeScale(Extent, Extent)}.
     *
     * @param source source extent (only X and Y dimensions are considered)
     * @param target target extent (only X and Y dimensions are considered)
     * @param preserveAspectRatio iff true, the aspect ratio is preserved, and {@link
     *     #relativeScalePreserveAspectRatio(Extent, Extent)} is called, otherwise {@link
     *     #relativeScale(Extent, Extent)}.
     * @return
     */
    public static ScaleFactor relativeScale(
            Extent source, Extent target, boolean preserveAspectRatio) {
        if (preserveAspectRatio) {
            return ResizeExtentUtilities.relativeScalePreserveAspectRatio(source, target);
        } else {
            return ResizeExtentUtilities.relativeScale(source, target);
        }
    }

    /**
     * Calculates a scaling factor so as to maximally scale {@code source} to {@code target} -
     * <b>while preserving the aspect ratio</b>.
     *
     * <p>Either the X or Y dimension is guaranteed to have a scale-factor {@code target / source},
     * and the other will scale so as not to exceed the size of {@code target}.
     *
     * @param source source extent (only X and Y dimensions are considered)
     * @param target target extent (only X and Y dimensions are considered)
     * @return the scaling-factor to scale the source to be the same size as the target
     */
    public static ScaleFactor relativeScalePreserveAspectRatio(Extent source, Extent target) {
        ScaleFactor withoutPreserving = relativeScale(source, target);
        double minDimension = withoutPreserving.minimumDimension();
        return new ScaleFactor(minDimension, minDimension);
    }

    /**
     * Calculates a scaling factor so as to scale {@code source} to {@code target}.
     *
     * <p>i.e. the scale-factor is {@code target / source} for each XY dimension.
     *
     * @param source source extent (only X and Y dimensions are considered)
     * @param target target extent (only X and Y dimensions are considered)
     * @return the scaling-factor to scale the source to be the same size as the target
     */
    public static ScaleFactor relativeScale(Extent source, Extent target) {
        return new ScaleFactor(
                Scaler.deriveScalingFactor(target.x(), source.x()),
                Scaler.deriveScalingFactor(target.y(), source.y()));
    }
}
