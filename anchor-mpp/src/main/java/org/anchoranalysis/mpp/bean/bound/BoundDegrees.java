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

package org.anchoranalysis.mpp.bean.bound;

import java.util.Optional;
import org.anchoranalysis.image.core.dimensions.Resolution;

/**
 * Represents an upper and lower bound in degrees which is converted to radians when resolved.
 *
 * <p>This class extends {@link BoundMinMax} to provide specific functionality for degree-based bounds,
 * automatically converting degrees to radians when resolving the bounds.</p>
 */
public class BoundDegrees extends BoundMinMax {

    private static final long serialVersionUID = 1281361242890359356L;

    /**
     * Creates a new instance with default bounds of 0 to 360 degrees.
     */
    public BoundDegrees() {
        super(0, 360);
    }

    /**
     * Creates a new instance by copying an existing {@link BoundDegrees}.
     *
     * @param source the source {@link BoundDegrees} to copy from
     */
    public BoundDegrees(BoundDegrees source) {
        super(source);
    }

    @Override
    public double getMinResolved(Optional<Resolution> resolution, boolean do3D) {
        return convertDegreesToRadians(getMin());
    }

    @Override
    public double getMaxResolved(Optional<Resolution> resolution, boolean do3D) {
        return convertDegreesToRadians(getMax());
    }

    @Override
    public Bound duplicate() {
        return new BoundDegrees(this);
    }

    /**
     * Converts degrees to radians.
     *
     * @param degrees the angle in degrees
     * @return the angle in radians
     */
    private static double convertDegreesToRadians(double degrees) {
        return (Math.PI / 180) * degrees;
    }
}