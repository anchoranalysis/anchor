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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.object.scaled.FromMask;
import org.anchoranalysis.overlay.object.scaled.ScaledOverlayCreator;
import org.anchoranalysis.overlay.writer.DrawOverlay;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * An implementation of {@link Overlay} that draws an {@link Mark} on an image.
 *
 * @author Owen feehan
 */
@EqualsAndHashCode(callSuper = false)
public class OverlayMark extends Overlay {

    @Getter private final Mark mark;
    private final RegionMembershipWithFlags regionMembership;

    @EqualsAndHashCode.Exclude private final ScaledOverlayCreator scaledMaskCreator;

    public OverlayMark(Mark mark, RegionMembershipWithFlags regionMembership) {
        this.mark = mark;
        this.regionMembership = regionMembership;

        /** How we create our scaled masks */
        scaledMaskCreator = new FromMask();
                new VolumeThreshold(
                        new FromMask(), // Above the threshold, we use the quick *rough* method for
                        // scaling up
                        new FromMark(
                                regionMembership), // Below the threshold, we use the slower *fine*
                        // method for scaling up
                        5000 // The threshold that decides which to use
                        );
    }

    @Override
    public BoundingBox box(DrawOverlay overlayWriter, Dimensions dim) {
        return mark.box(dim, regionMembership.getRegionID());
    }

    @Override
    public ObjectWithProperties createObject(
            DrawOverlay drawer, Dimensions dimEntireImage, BinaryValuesByte binaryValuesOut)
            throws CreateException {
        return new ObjectWithProperties(mark.deriveObject(dimEntireImage, regionMembership, binaryValuesOut));
    }

    public int getIdentifier() {
        return mark.getIdentifier();
    }
}
