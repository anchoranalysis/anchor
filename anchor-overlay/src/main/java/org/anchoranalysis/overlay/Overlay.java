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

package org.anchoranalysis.overlay;

import java.util.Optional;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.dimensions.Dimensions;
import org.anchoranalysis.image.dimensions.Resolution;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.overlay.id.Identifiable;
import org.anchoranalysis.overlay.writer.DrawOverlay;

// What can be projected on top of a raster through the GUI
public abstract class Overlay implements Identifiable {

    /**
     * A bounding-box around the overlay
     *
     * @param overlayWriter
     * @param dimensions The dimensions of the containing-scene
     * @return the bounding-box
     */
    public abstract BoundingBox box(DrawOverlay overlayWriter, Dimensions dimensions);

    public abstract ObjectWithProperties createScaleObject(
            DrawOverlay overlayWriter,
            double zoomFactorNew,
            ObjectWithProperties om,
            Overlay ol,
            Dimensions dimensionsUnscaled,
            Dimensions dimensionsScaled,
            BinaryValuesByte bvOut)
            throws CreateException;

    public abstract ObjectWithProperties createObject(
            DrawOverlay overlayWriter, Dimensions dimEntireImage, BinaryValuesByte bvOut)
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

    public abstract OverlayProperties generateProperties(Optional<Resolution> resolution);
}
