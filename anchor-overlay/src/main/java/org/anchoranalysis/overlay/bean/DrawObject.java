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

package org.anchoranalysis.overlay.bean;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.overlay.writer.PrecalculationOverlay;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * Draws a representation of an {@link ObjectMask} on a {@link RGBStack}.
 *
 * @author Owen Feehan
 */
public abstract class DrawObject extends AnchorBean<DrawObject> {

    /**
     * Draws a single-object on top of a RGB-stack
     *
     * @param object the object to draw.
     * @param stack the image to draw on.
     * @param attributes attributes for each object when drawing.
     * @param iteration the current iteration.
     * @param restrictTo a restriction on which part of stack we draw onto to (considered in terms
     *     of the possibly-zoomed pixel cooridinates).
     */
    public final void writeSingle(
            ObjectWithProperties object,
            RGBStack stack,
            ObjectDrawAttributes attributes,
            int iteration,
            BoundingBox restrictTo)
            throws OperationFailedException {

        try {
            PrecalculationOverlay precalculatedObj = precalculate(object, stack.dimensions());
            precalculatedObj.writePrecalculatedMask(stack, attributes, iteration, restrictTo);

        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    // Does computational preprocessing (so it can be cached). Outputs a collection of object-masks
    // that are later re used
    public abstract PrecalculationOverlay precalculate(
            ObjectWithProperties object, Dimensions dimensions) throws CreateException;

    public void write(
            ObjectCollectionWithProperties objects,
            RGBStack background,
            ObjectDrawAttributes attributes)
            throws OperationFailedException {
        write(objects, background, attributes, new BoundingBox(background.extent()));
    }

    /**
     * @param objects Masks to write
     * @param stack Stack to write masks on top of
     * @param attributes Extracts attributes from objects relevant to drawing
     * @param boxContainer A bounding box, which restricts where we write out to
     * @throws OperationFailedException
     */
    public void write(
            ObjectCollectionWithProperties objects,
            RGBStack stack,
            ObjectDrawAttributes attributes,
            BoundingBox boxContainer)
            throws OperationFailedException {
        // We iterate through every mark
        int i = 0;
        for (ObjectWithProperties object : objects) {
            writeSingle(object, stack, attributes, i++, boxContainer);
        }
    }
}
