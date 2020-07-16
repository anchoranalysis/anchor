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
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.anchor.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.anchor.overlay.writer.PrecalcOverlay;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

/**
 * Writes a cross at the midpoint of an object
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@AllArgsConstructor
public class Midpoint extends DrawObject {

    private static final String PROPERTY_MIDPOINT = "midpointInt";

    // START BEAN PROEPRTIES
    @BeanField @Getter @Setter private int extraLength = 2;
    // END BEAN PROPERTIES

    public static Point3i calcMidpoint(ObjectWithProperties mask, boolean suppressZ) {

        return maybeSuppressZ(calcMidpoint3D(mask), suppressZ);
    }

    @Override
    public PrecalcOverlay precalculate(ObjectWithProperties mask, ImageDimensions dim)
            throws CreateException {

        // We ignore the z-dimension so it's projectable onto a 2D slice
        Point3i midPoint = calcMidpoint(mask, true);

        return new PrecalcOverlay(mask) {

            @Override
            public void writePrecalculatedMask(
                    RGBStack background,
                    ObjectDrawAttributes attributes,
                    int iteration,
                    BoundingBox restrictTo)
                    throws OperationFailedException {

                writeCross(
                        midPoint,
                        attributes.colorFor(mask, iteration),
                        background,
                        extraLength,
                        restrictTo);
            }
        };
    }

    public static void writeRelPoint(
            Point3i point, RGBColor color, RGBStack stack, BoundingBox bboxContainer) {
        if (bboxContainer.contains().point(point)) {
            stack.writeRGBPoint(Point3i.immutableSubtract(point, bboxContainer.cornerMin()), color);
        }
    }

    public static void writeCross(
            Point3i midpoint,
            RGBColor color,
            RGBStack stack,
            int extraLength,
            BoundingBox bboxContainer) {

        if (!stack.getDimensions().contains(midpoint)) {
            return;
        }

        stack.writeRGBPoint(midpoint, color);

        // X direction
        for (int i = 0; i < extraLength; i++) {
            midpoint.decrementX();
            writeRelPoint(midpoint, color, stack, bboxContainer);
        }
        midpoint.incrementX(extraLength);

        for (int i = 0; i < extraLength; i++) {
            midpoint.incrementX();
            writeRelPoint(midpoint, color, stack, bboxContainer);
        }
        midpoint.decrementX(extraLength);

        // Y direction
        for (int i = 0; i < extraLength; i++) {
            midpoint.decrementY();
            writeRelPoint(midpoint, color, stack, bboxContainer);
        }
        midpoint.incrementY(extraLength);

        for (int i = 0; i < extraLength; i++) {
            midpoint.decrementY();
            writeRelPoint(midpoint, color, stack, bboxContainer);
        }
        midpoint.decrementY(extraLength);
    }

    private static Point3i maybeSuppressZ(Point3i point, boolean suppressZ) {
        if (suppressZ) {
            point.setZ(0);
        }
        return point;
    }

    private static Point3i calcMidpoint3D(ObjectWithProperties mask) {
        if (mask.hasProperty(PROPERTY_MIDPOINT)) {
            return Point3i.immutableAdd(
                    (Point3i) mask.getProperty(PROPERTY_MIDPOINT),
                    mask.getBoundingBox().cornerMin());
        } else {
            return PointConverter.intFromDouble(mask.getMask().centerOfGravity());
        }
    }
}
