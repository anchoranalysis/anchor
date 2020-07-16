/* (C)2020 */
package org.anchoranalysis.anchor.overlay.object;

import lombok.Getter;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.OverlayProperties;
import org.anchoranalysis.anchor.overlay.object.scaled.FromMask;
import org.anchoranalysis.anchor.overlay.object.scaled.ScaledMaskCreator;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;

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

    // Assumes object mask is always inside the dim. TODO verify that is valid.
    @Override
    public BoundingBox bbox(DrawOverlay overlayWriter, ImageDimensions dim) {
        return object.getBoundingBox();
    }

    @Override
    public ObjectWithProperties createScaledMask(
            DrawOverlay overlayWriter,
            double zoomFactorNew,
            ObjectWithProperties om,
            Overlay ol,
            ImageDimensions sdUnscaled,
            ImageDimensions sdScaled,
            BinaryValuesByte bvOut)
            throws CreateException {

        return SCALED_MASK_CREATOR.createScaledMask(
                overlayWriter, om, zoomFactorNew, om, sdScaled, bvOut);
    }

    // TODO do we need to duplicate here?
    @Override
    public ObjectWithProperties createObject(
            DrawOverlay overlayWriter, ImageDimensions dimEntireImage, BinaryValuesByte bvOut)
            throws CreateException {
        return object;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean isPointInside(DrawOverlay overlayWriter, Point3i point) {
        return object.getMask().contains(point);
    }

    // We delegate uniqueness-check to the mask
    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof OverlayObjectMask) {
            OverlayObjectMask objCast = (OverlayObjectMask) arg0;
            return this.object.getMask().equals(objCast.object.getMask());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return object.getMask().hashCode();
    }

    @Override
    public OverlayProperties generateProperties(ImageResolution sr) {
        // TODO take the properties from the object mask
        OverlayProperties out = new OverlayProperties();
        out.add("id", id);
        return out;
    }
}
