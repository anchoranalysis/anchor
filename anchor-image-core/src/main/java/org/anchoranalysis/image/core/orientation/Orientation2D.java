/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.core.orientation;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.value.SimpleNameValue;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.spatial.rotation.RotationMatrix;
import org.anchoranalysis.spatial.rotation.RotationMatrix2DFromRadianCreator;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Orientation2D extends Orientation {

    /** */
    private static final long serialVersionUID = 1528190376087281572L;

    @Getter @Setter private double angleRadians = 0;

    public double getAngleDegrees() {
        return angleRadians * 180 / Math.PI;
    }

    public void rotate(double rotateRadians) {
        this.angleRadians += rotateRadians;
    }

    @Override
    public Orientation2D negative() {
        return new Orientation2D((this.angleRadians + Math.PI) % (2 * Math.PI));
    }

    @Override
    public Orientation2D duplicate() {
        return new Orientation2D(angleRadians);
    }

    @Override
    public RotationMatrix createRotationMatrix() {
        return new RotationMatrix2DFromRadianCreator(angleRadians).createRotationMatrix();
    }

    @Override
    public String toString() {
        return String.format("[rad=%f]", angleRadians);
    }

    @Override
    public void addProperties(NameValueSet<String> namedValues) {
        addAngleProperty(namedValues, "radians", getAngleRadians());
        addAngleProperty(namedValues, "degrees", getAngleDegrees());
    }

    @Override
    public void addPropertiesToMask(ObjectWithProperties object) {
        object.setProperty("orientationRadians", getAngleRadians());
    }

    @Override
    public int numberDimensions() {
        return 2;
    }

    private static void addAngleProperty(
            NameValueSet<String> namedValues, String unitType, double angleValue) {
        namedValues.add(
                new SimpleNameValue<>(
                        String.format("Orientation Angle (%s)", unitType),
                        String.format("%1.2f", angleValue)));
    }
}
