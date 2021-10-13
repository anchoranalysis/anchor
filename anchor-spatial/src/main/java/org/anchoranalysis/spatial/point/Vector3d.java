package org.anchoranalysis.spatial.point;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/*
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

/**
 * A <i>three</i>-dimensional vector of <i>double</i> values.
 *
 * <p>We consider a vector to be a tuple with additional magnitude and direction, to give a physical
 * interpretation.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class Vector3d extends Tuple3d {

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Creates with the same values as an existing {@link Tuple3d}.
     *
     * @param tuple to copy values from.
     */
    public Vector3d(Tuple3d tuple) {
        this.x = tuple.x;
        this.y = tuple.y;
        this.z = tuple.z;
    }

    /**
     * Create with values for each dimension.
     * 
     * @param x the value for the X-dimension.
     * @param y the value for the Y-dimension.
     * @param z the value for the Z-dimension.
     */
    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * The magnitude of the vector.
     * 
     * @return the vector's length.
     */
    public double length() {
        double squared = (x * x) + (y * y) + (z * z);
        return Math.sqrt(squared);
    }

    /**
     * Normalizes the vector by dividing each component's value by the overall length.
     */
    public void normalize() {
        double length = length();
        this.x /= length;
        this.y /= length;
        this.z /= length;
    }
    
    /**
     * Computes the <a href="https://en.wikipedia.org/wiki/Dot_product">dot-product</a> of the tuple with another.
     * 
     * @param other the other tuple to use in the dot product operation.
     * @return the computed dot-product.
     */
    public final double dotProduct(Tuple3d other) {
        return (x * other.x) + (y * other.y) + (z * other.z);
    }

    /**
     * Computes the <a href="https://en.wikipedia.org/wiki/Cross_product">cross-product</a> of the tuple with another.
     * 
     * @param other the other tuple to use in the dot product operation.
     * @return the computed cross-product.
     */
    public Vector3d crossProduct(Vector3d other) {
        Vector3d out = new Vector3d();
        out.x = y() * other.z() - z() * other.y();
        out.y = z() * other.x() - x() * other.z();
        out.z = x() * other.y() - y() * other.x();
        return out;
    }
}
