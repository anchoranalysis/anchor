/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.overlay;

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.overlay.object.scaled.ScaledOverlayCreator;
import org.anchoranalysis.overlay.writer.DrawOverlay;

@AllArgsConstructor
class VolumeThreshold implements ScaledOverlayCreator {

    private ScaledOverlayCreator greaterThanEqualThreshold;
    private ScaledOverlayCreator lessThreshold;
    private int threshold;

    @Override
    public ObjectWithProperties createScaledObject(
            DrawOverlay overlayWriter,
            ObjectWithProperties unscaled,
            double scaleFactor,
            Object originalObject,
            Dimensions dimensionsScaled,
            BinaryValuesByte bvOut)
            throws CreateException {

        Mark originalMark = (Mark) originalObject;

        // Note hardcoded to use region 0
        double zoomVolume = originalMark.volume(0) * Math.pow(scaleFactor, 2);

        if (zoomVolume > threshold) {
            return greaterThanEqualThreshold.createScaledObject(
                    overlayWriter, unscaled, scaleFactor, originalObject, dimensionsScaled, bvOut);
        } else {
            return lessThreshold.createScaledObject(
                    overlayWriter, unscaled, scaleFactor, originalObject, dimensionsScaled, bvOut);
        }
    }
}
