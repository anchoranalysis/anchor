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
 * Draws the outline of each object-mask.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class Outline extends DrawObject {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private int outlineWidth;

    @BeanField @Getter @Setter private boolean force2D = false;
    // END BEAN PROPERTIES

    public Outline() {
        this(1);
    }

    public Outline(int outlineWidth) {
        this(outlineWidth, false);
    }

    @Override
    public PrecalcOverlay precalculate(ObjectWithProperties object, ImageDimensions dim)
            throws CreateException {

        ObjectMask outline =
                FindOutline.outline(
                        object.withoutProperties(), outlineWidth, true, (dim.getZ() > 1) && !force2D);

        ObjectWithProperties objectWithProperties =
                new ObjectWithProperties(outline, object.getProperties());

        return new PrecalcOverlay(object) {

            @Override
            public void writePrecalculatedMask(
                    RGBStack background,
                    ObjectDrawAttributes attributes,
                    int iteration,
                    BoundingBox restrictTo)
                    throws OperationFailedException {

                assert (outline.getVoxels().extent().getZ() > 0);
                // TODO this can get broken! Fix!
                assert (outline.getBoundingBox().cornerMin().getZ() >= 0);

                IntersectionWriter.writeRGBMaskIntersection(
                        outline,
                        attributes.colorFor(objectWithProperties, iteration),
                        background,
                        restrictTo);
            }
        };
    }
}
