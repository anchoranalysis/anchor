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

package org.anchoranalysis.overlay.object;

import java.util.Optional;
import lombok.Getter;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.OverlayProperties;
import org.anchoranalysis.overlay.object.scaled.FromMask;
import org.anchoranalysis.overlay.object.scaled.ScaledMaskCreator;
import org.anchoranalysis.overlay.writer.DrawOverlay;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3i;

public class OverlayObjectMask extends Overlay {

    private static final ScaledMaskCreator SCALED_MASK_CREATOR = new FromMask();

    @Getter private final ObjectWithProperties object;

    /** ID associated with object */
    private final int id;

    public OverlayObjectMask(ObjectMask object, int id) {
        super();
        this.object = new ObjectWithProperties(object);
        this.object.getProperties().put("id", id);
        this.id = id;
    }

    // Assumes object-mask is always inside the dim. TODO verify that is valid.
    @Override
    public BoundingBox box(DrawOverlay overlayWriter, Dimensions dim) {
        return object.boundingBox();
    }

    @Override
    public ObjectWithProperties createScaleObject(
            DrawOverlay overlayWriter,
            double zoomFactorNew,
            ObjectWithProperties om,
            Overlay ol,
            Dimensions dimensionsUnscaled,
            Dimensions dimensionsScaled,
            BinaryValuesByte bvOut)
            throws CreateException {

        return SCALED_MASK_CREATOR.createScaledMask(
                overlayWriter, om, zoomFactorNew, om, dimensionsScaled, bvOut);
    }

    // TODO do we need to duplicate here?
    @Override
    public ObjectWithProperties createObject(
            DrawOverlay overlayWriter, Dimensions dimEntireImage, BinaryValuesByte bvOut)
            throws CreateException {
        return object;
    }

    @Override
    public int getIdentifier() {
        return id;
    }

    @Override
    public boolean isPointInside(DrawOverlay overlayWriter, Point3i point) {
        return object.withoutProperties().contains(point);
    }

    // We delegate uniqueness-check to the object-mask
    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof OverlayObjectMask) {
            OverlayObjectMask objCast = (OverlayObjectMask) arg0;
            return this.object.withoutProperties().equals(objCast.object.withoutProperties());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return object.withoutProperties().hashCode();
    }

    @Override
    public OverlayProperties generateProperties(Optional<Resolution> resolution) {
        // TODO take the properties from the object-mask
        OverlayProperties out = new OverlayProperties();
        out.add("id", id);
        return out;
    }
}
