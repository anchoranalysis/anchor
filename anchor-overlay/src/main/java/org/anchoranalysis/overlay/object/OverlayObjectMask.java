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

import lombok.Getter;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.writer.DrawOverlay;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * An implementation of {@link Overlay} that draws an {@link ObjectMask} on an image.
 *
 * @author Owen Feehan
 */
public class OverlayObjectMask extends Overlay {

    /** The {@link ObjectMask} to draw. */
    @Getter private final ObjectWithProperties object;

    /**
     * Creates with a particular {@link ObjectMask} and identifier.
     *
     * @param object the object-mask.
     * @param id the identifier.
     */
    public OverlayObjectMask(ObjectMask object, int id) {
        this.object = new ObjectWithProperties(object);
    }

    @Override
    public BoundingBox box(DrawOverlay overlayWriter, Dimensions dimensions) {
        // Assumes the object-mask is always inside the dimensions.
        return object.boundingBox();
    }

    @Override
    public ObjectWithProperties createObject(
            Dimensions dimensionsEntireImage, BinaryValuesByte binaryValuesOut)
            throws CreateException {
        return object;
    }

    // We delegate uniqueness-check to the object-mask
    @Override
    public boolean equals(Object other) {
        if (other instanceof OverlayObjectMask otherCast) {
            return this.object.asObjectMask().equals(otherCast.object.asObjectMask());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return object.asObjectMask().hashCode();
    }
}
