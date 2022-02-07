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
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.contour.FindContour;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.overlay.bean.DrawObject;
import org.anchoranalysis.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.overlay.writer.PrecalculationOverlay;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * Draws the outline of the bounding-box for each object.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class BoundingBoxOutline extends DrawObject {

    // START BEAN PROPERTIES
    /** How many voxels are used for the outline of the bounding-box. */
    @BeanField @Getter @Setter private int outlineWidth;
    // END BEAN PROPERTIES

    /** Creates with an outline-width of 1 voxel. */
    public BoundingBoxOutline() {
        this(1);
    }

    @Override
    public PrecalculationOverlay precalculate(ObjectWithProperties object, Dimensions dim)
            throws CreateException {
        ObjectMask outline =
                FindContour.createFrom(
                        createBoundingBoxObject(object.asObjectMask()),
                        outlineWidth,
                        dim.z() > 1,
                        true);

        return new PrecalculationOverlay(object) {

            @Override
            public void writePrecalculatedMask(
                    RGBStack background,
                    ObjectDrawAttributes attributes,
                    int iteration,
                    BoundingBox restrictTo)
                    throws OperationFailedException {

                IntersectionWriter.writeRGBMaskIntersection(
                        outline, attributes.colorFor(object, iteration), background, restrictTo);
            }
        };
    }

    /** Creates a {@link ObjectMask} whose entire bounding-box has <i>on</i> voxels. */
    private ObjectMask createBoundingBoxObject(ObjectMask object) {
        ObjectMask mask = object.duplicate();
        mask.assignOn().toAll();
        return mask;
    }
}
