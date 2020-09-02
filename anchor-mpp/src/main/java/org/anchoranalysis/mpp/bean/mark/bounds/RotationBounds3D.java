/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.bean.mark.bounds;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.Resolution;
import org.anchoranalysis.image.orientation.Orientation;
import org.anchoranalysis.image.orientation.Orientation3DEulerAngles;
import org.anchoranalysis.mpp.bean.bound.Bound;
import org.anchoranalysis.mpp.bean.bound.BoundUnitless;

/**
 * Creates a randomly-generated orientation in 3D based upon Euler Angles
 *
 * @author Owen Feehan
 */
public class RotationBounds3D extends RotationBounds {

    private static final Bound DEFAULT_BOUND = new BoundUnitless(0, 2 * Math.PI);

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private Bound rotationX = DEFAULT_BOUND;

    @BeanField @Getter @Setter private Bound rotationY = DEFAULT_BOUND;

    @BeanField @Getter @Setter private Bound rotationZ = DEFAULT_BOUND;
    // END BEAN PROPERTIES

    @Override
    public Orientation randomOrientation(
            RandomNumberGenerator randomNumberGenerator, Resolution resolution) {
        return new Orientation3DEulerAngles(
                randomizeRotation(rotationX, randomNumberGenerator, resolution),
                randomizeRotation(rotationY, randomNumberGenerator, resolution),
                randomizeRotation(rotationZ, randomNumberGenerator, resolution));
    }

    @Override
    public String describeBean() {
        return String.format(
                "%s, rotation=(%f,%f,%f)",
                getBeanName(), getRotationX(), getRotationY(), rotationZ);
    }

    private static double randomizeRotation(
            Bound bound, RandomNumberGenerator randomNumberGenerator, Resolution resolution) {
        return bound.resolve(resolution, true).randOpen(randomNumberGenerator);
    }
}
