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
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.io.object.ExtractMidpoint;
import org.anchoranalysis.image.voxel.object.ObjectMask; // NOSONAR
import org.anchoranalysis.overlay.bean.DrawObject;
import org.anchoranalysis.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.overlay.writer.PrecalculationOverlay;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Draws a cross at the midpoint of an {@link ObjectMask}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@AllArgsConstructor
public class Midpoint extends DrawObject {

    // START BEAN PROPERTIES
    /**
     * How long the cross extends on one-side (not considering the center pixel).
     *
     * <p>e.g. a value of 2, means the cross has 5 {@code ((2*2)+1)} voxels width and height in
     * total.
     *
     * <p>e.g. a value of 5, means the cross has 21 {@code ((2*10)+1)} voxels width and height in
     * total.
     */
    @BeanField @Getter @Setter private int extraLength = 2;
    // END BEAN PROPERTIES

    @Override
    public PrecalculationOverlay precalculate(ObjectWithProperties object, Dimensions dim)
            throws CreateException {

        // We ignore the z-dimension so that it's projectable onto a 2D slice.
        Point3i midPoint = ExtractMidpoint.midpoint(object, true);

        return new PrecalculationOverlay(object) {

            @Override
            public void writePrecalculatedMask(
                    RGBStack background,
                    ObjectDrawAttributes attributes,
                    int iteration,
                    BoundingBox restrictTo)
                    throws OperationFailedException {

                drawCross(
                        midPoint,
                        attributes.colorFor(object, iteration),
                        background,
                        extraLength,
                        restrictTo);
            }
        };
    }

    /**
     * Draws a cross at {@code midpoint} on the {@code box} extracted region {@code stack}, where
     * the point is expressed in global-coordinates.
     */
    private static void drawCross(
            Point3i midpoint, RGBColor color, RGBStack stack, int extraLength, BoundingBox box) {

        if (!stack.dimensions().contains(midpoint)) {
            return;
        }

        stack.assignVoxel(midpoint, color);

        // X direction
        for (int i = 0; i < extraLength; i++) {
            midpoint.decrementX();
            drawPoint(midpoint, color, stack, box);
        }
        midpoint.incrementX(extraLength);

        for (int i = 0; i < extraLength; i++) {
            midpoint.incrementX();
            drawPoint(midpoint, color, stack, box);
        }
        midpoint.decrementX(extraLength);

        // Y direction
        for (int i = 0; i < extraLength; i++) {
            midpoint.decrementY();
            drawPoint(midpoint, color, stack, box);
        }
        midpoint.incrementY(extraLength);

        for (int i = 0; i < extraLength; i++) {
            midpoint.decrementY();
            drawPoint(midpoint, color, stack, box);
        }
        midpoint.decrementY(extraLength);
    }

    /**
     * Writes a {@code point} on the {@code box} extracted region {@code stack}, where the point is
     * expressed in global-coordinates.
     */
    private static void drawPoint(Point3i point, RGBColor color, RGBStack stack, BoundingBox box) {
        if (box.contains().point(point)) {
            stack.assignVoxel(Point3i.immutableSubtract(point, box.cornerMin()), color);
        }
    }
}
