/*-
 * #%L
 * anchor-io
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

package org.anchoranalysis.image.io.bean.object.draw;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.contour.FindContour;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.overlay.bean.DrawObject;
import org.anchoranalysis.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * Draws the outline of each object-mask.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class Outline extends DrawObject {

    // START BEAN PROPERTIES
    /** The size of the outline, in terms of the number of voxels. */
    @BeanField @Getter @Setter private int outlineWidth;

    /**
     * If true the outline is also applied in the z-dimension, otherwise this is ignored as possible
     * boundary
     */
    @BeanField @Getter @Setter private boolean includeZ;

    // END BEAN PROPERTIES

    /** Creates with an outline-width of 1 voxel. */
    public Outline() {
        this(1);
    }

    /**
     * Create with a particular outline-width.
     *
     * @param outlineWidth the size of the outline, in terms of the number of voxels.
     */
    public Outline(int outlineWidth) {
        this(outlineWidth, true);
    }

    @Override
    public void drawSingle(
            ObjectWithProperties object,
            RGBStack stack,
            ObjectDrawAttributes attributes,
            int iteration,
            BoundingBox restrictTo)
            throws OperationFailedException {
        ObjectMask outline =
                FindContour.createFrom(
                        object.asObjectMask(),
                        outlineWidth,
                        (stack.dimensions().z() > 1) && includeZ,
                        true);

        ObjectWithProperties objectWithProperties =
                new ObjectWithProperties(outline, object.getProperties());

        IntersectionWriter.writeRGBMaskIntersection(
                outline, attributes.colorFor(objectWithProperties, iteration), stack, restrictTo);
    }
}
