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

package org.anchoranalysis.anchor.mpp.overlay;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.overlay.object.scaled.ScaledMaskCreator;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;

class FromMark implements ScaledMaskCreator {

    private RegionMembershipWithFlags regionMembership;

    public FromMark(RegionMembershipWithFlags regionMembership) {
        super();
        this.regionMembership = regionMembership;
    }

    @Override
    public ObjectWithProperties createScaledMask(
            DrawOverlay overlayWriter,
            ObjectWithProperties unscaled,
            double scaleFactor,
            Object originalObject,
            Dimensions dimensionsScaled,
            BinaryValuesByte bv)
            throws CreateException {

        Mark originalMark = (Mark) originalObject;

        ObjectWithProperties omScaled =
                originalMark.maskScaledXY(dimensionsScaled, regionMembership, bv, scaleFactor);

        // We keep the properties the same
        return new ObjectWithProperties(omScaled.withoutProperties(), unscaled.getProperties());
    }
}
