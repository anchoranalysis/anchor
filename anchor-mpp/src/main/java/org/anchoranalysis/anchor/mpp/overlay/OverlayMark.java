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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.OverlayProperties;
import org.anchoranalysis.anchor.overlay.object.scaled.FromMask;
import org.anchoranalysis.anchor.overlay.object.scaled.ScaledMaskCreator;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;

@EqualsAndHashCode(callSuper = false)
public class OverlayMark extends Overlay {

    @Getter private final Mark mark;
    private final RegionMembershipWithFlags regionMembership;

    @EqualsAndHashCode.Exclude private final ScaledMaskCreator scaledMaskCreator;

    public OverlayMark(Mark mark, RegionMembershipWithFlags regionMembership) {
        super();
        this.mark = mark;
        this.regionMembership = regionMembership;

        /** How we create our scaled masks */
        scaledMaskCreator =
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
    public BoundingBox bbox(DrawOverlay overlayWriter, ImageDimensions dim) {
        return mark.bbox(dim, regionMembership.getRegionID());
    }

    @Override
    public ObjectWithProperties createScaleObject(
            DrawOverlay overlayWriter,
            double zoomFactorNew,
            ObjectWithProperties om,
            Overlay ol,
            ImageDimensions sdUnscaled,
            ImageDimensions sdScaled,
            BinaryValuesByte bvOut)
            throws CreateException {

        return scaledMaskCreator.createScaledMask(
                overlayWriter, om, zoomFactorNew, mark, sdUnscaled, bvOut);
    }

    @Override
    public ObjectWithProperties createObject(
            DrawOverlay overlayWriter, ImageDimensions dimEntireImage, BinaryValuesByte bvOut)
            throws CreateException {
        return mark.deriveObject(dimEntireImage, regionMembership, bvOut);
    }

    @Override
    public int getId() {
        return mark.getId();
    }

    @Override
    public boolean isPointInside(DrawOverlay overlayWriter, Point3i point) {

        Point3d pointD = PointConverter.doubleFromInt(point);

        byte membership = mark.evalPointInside(pointD);
        return (regionMembership.isMemberFlag(membership));
    }

    @Override
    public OverlayProperties generateProperties(ImageResolution sr) {
        return mark.generateProperties(sr);
    }
}
