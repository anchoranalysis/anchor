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

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.core.stack.rgb.RGBStack;
import org.anchoranalysis.overlay.bean.DrawObject;
import org.anchoranalysis.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.overlay.writer.PrecalculationOverlay;
import org.anchoranalysis.spatial.extent.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Writes a line through the midpoint illustrating the orientation of the object
 *
 * @author Owen Feehan
 */
public class Orientation extends DrawObject {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private double xDiv = 1;

    @BeanField @Getter @Setter private boolean drawReverseLine = false;
    // END BEAN PROPERTIES

    public static Optional<Point3d> calculatePoint(
            ObjectWithProperties object, String propertyName) {

        if (!object.hasProperty(propertyName)) {
            return Optional.empty();
        }

        return Optional.of(new Point3d((Point3d) object.getProperty(propertyName)));
    }

    public static Optional<Double> calculateOrientation(ObjectWithProperties object) {

        if (!object.hasProperty("orientationRadians")) {
            return Optional.empty();
        }

        return Optional.of((Double) object.getProperty("orientationRadians"));
    }

    @Override
    public PrecalculationOverlay precalculate(ObjectWithProperties object, Dimensions dim)
            throws CreateException {
        return new PrecalculationOverlay(object) {

            @Override
            public void writePrecalculatedMask(
                    RGBStack background,
                    ObjectDrawAttributes attributes,
                    int iteration,
                    BoundingBox restrictTo)
                    throws OperationFailedException {

                Point3i midpoint = Midpoint.calculateMidpoint(object, false);

                Optional<Double> orientationRadians = calculateOrientation(object);
                if (!orientationRadians.isPresent()) {
                    return;
                }

                Optional<Point3d> xAxisMin = calculatePoint(object, "xAxisMin");
                if (!xAxisMin.isPresent()) {
                    return;
                }

                Optional<Point3d> xAxisMax = calculatePoint(object, "xAxisMax");
                if (!xAxisMax.isPresent()) {
                    return;
                }

                RGBColor color = attributes.colorFor(object, iteration);

                writeOrientationLine(
                        midpoint, orientationRadians.get(), color, background, restrictTo);

                // Reverse
                if (drawReverseLine) {
                    writeOrientationLine(
                            midpoint, orientationRadians.get(), color, background, restrictTo);
                }
            }
        };
    }

    private void writeOrientationLine(
            Point3i midpoint,
            double orientationRadians,
            RGBColor color,
            RGBStack stack,
            BoundingBox box) {

        // We start at 0
        double x = midpoint.x();
        double y = midpoint.y();

        double xIncr = Math.cos(orientationRadians) * xDiv;
        double yIncr = Math.sin(orientationRadians) * xDiv;

        while (true) {
            Point3i point = new Point3i((int) x, (int) y, 0);

            if (box.contains().pointIgnoreZ(point)) {
                Midpoint.writeRelPoint(point, color, stack, box);

                x += xIncr;
                y += yIncr;
            } else {
                break;
            }
        }
    }
}
