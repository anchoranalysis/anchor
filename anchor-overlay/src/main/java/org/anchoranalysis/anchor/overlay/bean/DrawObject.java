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
/* (C)2020 */
package org.anchoranalysis.anchor.overlay.bean;

import org.anchoranalysis.anchor.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.anchor.overlay.writer.PrecalcOverlay;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

/**
 * Draws an object-mask on a RGB-stack
 *
 * @author Owen Feehan
 * @param <T> pre-calculate object type
 */
public abstract class DrawObject extends AnchorBean<DrawObject> {

    /**
     * Draws a single-object on top of a RGB-stack
     *
     * @param mask the object to draw
     * @param stack the RGB-stack to draw upon
     * @param attributes attributes for each object when drawing
     * @param iteration the current iteration
     * @param restrictTo a restriction on which part of stack we draw onto to (considered in terms
     *     of the possibly-zoomed pixel cooridinates)
     */
    public final void writeSingle(
            ObjectWithProperties mask,
            RGBStack stack,
            ObjectDrawAttributes attributes,
            int iteration,
            BoundingBox restrictTo)
            throws OperationFailedException {

        try {
            PrecalcOverlay precalculatedObj = precalculate(mask, stack.getDimensions());
            precalculatedObj.writePrecalculatedMask(stack, attributes, iteration, restrictTo);

        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    // Does computational preprocessing (so it can be cached). Outputs a collection of object-masks
    // that are later re used
    public abstract PrecalcOverlay precalculate(
            ObjectWithProperties object, ImageDimensions dimensions) throws CreateException;

    public void write(
            ObjectCollectionWithProperties masks,
            RGBStack background,
            ObjectDrawAttributes attributes)
            throws OperationFailedException {
        write(
                masks,
                background,
                attributes,
                new BoundingBox(background.getDimensions().getExtent()));
    }

    /**
     * @param masks Masks to write
     * @param stack Stack to write masks on top of
     * @param attributes Extracts attributes from objects relevant to drawing
     * @param bboxContainer A bounding box, which restricts where we write out to
     * @throws OperationFailedException
     */
    public void write(
            ObjectCollectionWithProperties masks,
            RGBStack stack,
            ObjectDrawAttributes attributes,
            BoundingBox bboxContainer)
            throws OperationFailedException {
        // We iterate through every mark
        int i = 0;
        for (ObjectWithProperties mask : masks) {
            writeSingle(mask, stack, attributes, i++, bboxContainer);
        }
    }
}
