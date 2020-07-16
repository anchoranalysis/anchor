/* (C)2020 */
package org.anchoranalysis.anchor.overlay;

import org.anchoranalysis.anchor.overlay.id.Identifiable;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;

// What can be projected on top of a raster through the GUI
public abstract class Overlay implements Identifiable {

    /**
     * A bounding-box around the overlay
     *
     * @param overlayWriter
     * @param dimensions The dimensions of the containing-scene
     * @return the bounding-box
     */
    public abstract BoundingBox bbox(DrawOverlay overlayWriter, ImageDimensions dimensions);

    public abstract ObjectWithProperties createScaledMask(
            DrawOverlay overlayWriter,
            double zoomFactorNew,
            ObjectWithProperties om,
            Overlay ol,
            ImageDimensions sdUnscaled,
            ImageDimensions sdScaled,
            BinaryValuesByte bvOut)
            throws CreateException;

    public abstract ObjectWithProperties createObject(
            DrawOverlay overlayWriter, ImageDimensions dimEntireImage, BinaryValuesByte bvOut)
            throws CreateException;

    /**
     * Is a point inside an overlay? (for a particular OverlayWriter).
     *
     * @param overlayWriter
     * @param point
     * @return
     */
    public abstract boolean isPointInside(DrawOverlay overlayWriter, Point3i point);

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    public abstract OverlayProperties generateProperties(ImageResolution sr);
}
