/*-
 * #%L
 * anchor-overlay
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

package org.anchoranalysis.overlay.object.scaled;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.overlay.writer.DrawOverlay;

/**
 * Creates a scaled version of a mask from a mark/object that is not scaled
 *
 * @author Owen Feehan
 */
public interface ScaledMaskCreator {

    /**
     * Creates a scaled-version of the mask
     *
     * @param overlayWriter what writes an overlay onto a raster
     * @param unscaled unscaled object-mask
     * @param scaleFactor how much to scale by (e.g. 0.5 scales the X dimension to 50%)
     * @param originalObject the object from which omUnscaled was derived
     * @param dimensionsScaled the scene-dimensions when scaled to match scaleFactor
     * @param bv binary-values for creating the mask
     * @return the scaled object-mask
     * @throws CreateException
     */
    ObjectWithProperties createScaledMask(
            DrawOverlay overlayWriter,
            ObjectWithProperties unscaled,
            double scaleFactor,
            Object originalObject,
            Dimensions dimensionsScaled,
            BinaryValuesByte bv)
            throws CreateException;
}
