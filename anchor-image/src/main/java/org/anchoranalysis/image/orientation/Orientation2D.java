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
/* (C)2020 */
package org.anchoranalysis.image.orientation;

import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.value.SimpleNameValue;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.math.rotation.RotationMatrix;
import org.anchoranalysis.math.rotation.RotationMatrix2DFromRadianCreator;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Orientation2D extends Orientation {

    /** */
    private static final long serialVersionUID = 1528190376087281572L;

    private double angleRadians = 0;

    public Orientation2D() {
        super();
    }

    public Orientation2D(double angleRadians) {
        super();
        this.angleRadians = angleRadians;
    }

    public double getAngleRadians() {
        return angleRadians;
    }

    public double getAngleDegrees() {
        return angleRadians * 180 / Math.PI;
    }

    public void setAngleRadians(double angleRadians) {
        this.angleRadians = angleRadians;
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
        Orientation2D copy = new Orientation2D();
        copy.angleRadians = this.angleRadians;
        return copy;
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
    public void addProperties(NameValueSet<String> nvc) {
        nvc.add(
                new SimpleNameValue<>(
                        "Orientation Angle (radians)", String.format("%1.2f", getAngleRadians())));
        nvc.add(
                new SimpleNameValue<>(
                        "Orientation Angle (degrees)", String.format("%1.2f", getAngleDegrees())));
    }

    @Override
    public void addPropertiesToMask(ObjectWithProperties mask) {
        mask.setProperty("orientationRadians", getAngleRadians());
    }

    @Override
    public int getNumDims() {
        return 2;
    }

    @Override
    public boolean equals(Object other) {

        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }

        if (!(other instanceof Orientation2D)) {
            return false;
        }

        Orientation2D otherCast = (Orientation2D) other;

        return angleRadians == otherCast.angleRadians;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(angleRadians).toHashCode();
    }
}
