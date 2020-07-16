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

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.value.SimpleNameValue;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.math.rotation.RotationMatrix;
import org.anchoranalysis.math.rotation.RotationMatrix3DFromRadianCreator;

// Conventions taken from http://mathworld.wolfram.com/EulerAngles.html
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Orientation3DEulerAngles extends Orientation {

    /** */
    private static final long serialVersionUID = -850189653607136128L;

    /** Rotation around X-dimension (in radians). Alpha. */
    private final double rotationX;

    /** Rotation around Y-dimension (in radians). Beta. */
    private final double rotationY;

    /** Rotation around Z-dimension (in radians). Gamma. */
    private final double rotationZ;

    public Orientation3DEulerAngles() {
        this(0.0, 0.0, 0.0);
    }

    @Override
    public Orientation3DEulerAngles duplicate() {
        return new Orientation3DEulerAngles(rotationX, rotationY, rotationZ);
    }

    @Override
    public String toString() {
        return String.format("%3.3f, %3.3f, %3.3f", rotationX, rotationY, rotationZ);
    }

    @Override
    public RotationMatrix createRotationMatrix() {
        return new RotationMatrix3DFromRadianCreator(rotationX, rotationY, rotationZ)
                .createRotationMatrix();
    }

    @Override
    public Orientation negative() {
        return new Orientation3DEulerAngles(
                rotationX, rotationY, (rotationZ + Math.PI) % (2 * Math.PI));
    }

    @Override
    public void addProperties(NameValueSet<String> nvc) {
        addProperty(nvc, "X", rotationX);
        addProperty(nvc, "Y", rotationY);
        addProperty(nvc, "Z", rotationZ);
    }

    private void addProperty(NameValueSet<String> nvc, String dimension, double radians) {
        nvc.add(
                new SimpleNameValue<>(
                        String.format("Orientation Angle %s (radians)", dimension),
                        String.format("%1.2f", radians)));
    }

    @Override
    public void addPropertiesToMask(ObjectWithProperties mask) {
        // NOTHING TO ADD
    }

    @Override
    public int getNumDims() {
        return 3;
    }
}
