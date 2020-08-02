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

package org.anchoranalysis.io.bean.object.writer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.anchor.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.anchor.overlay.writer.PrecalcOverlay;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.outline.FindOutline;
import org.anchoranalysis.image.stack.rgb.RGBStack;

/**
 * Draws the outline of the bounding-box for each object.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class BoundingBoxOutline extends DrawObject {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private int outlineWidth;
    // END BEAN PROPERTIES

    public BoundingBoxOutline() {
        this(1);
    }

    @Override
    public PrecalcOverlay precalculate(ObjectWithProperties mask, ImageDimensions dim)
            throws CreateException {
        ObjectMask outline =
                FindOutline.outline(
                        createBoundingBoxObject(mask.getMask()),
                        outlineWidth,
                        true,
                        dim.getZ() > 1);

        return new PrecalcOverlay(mask) {

            @Override
            public void writePrecalculatedMask(
                    RGBStack background,
                    ObjectDrawAttributes attributes,
                    int iteration,
                    BoundingBox restrictTo)
                    throws OperationFailedException {

                IntersectionWriter.writeRGBMaskIntersection(
                        outline, attributes.colorFor(mask, iteration), background, restrictTo);
            }
        };
    }

    private ObjectMask createBoundingBoxObject(ObjectMask mask) {
        ObjectMask bbox = mask.duplicate();
        bbox.getVoxelBox().setAllPixelsTo(1);
        return bbox;
    }
}