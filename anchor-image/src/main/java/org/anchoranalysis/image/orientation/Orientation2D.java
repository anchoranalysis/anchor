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
