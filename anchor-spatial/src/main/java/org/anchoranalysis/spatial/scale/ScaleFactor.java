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

package org.anchoranalysis.spatial.scale;

import com.google.common.base.Preconditions;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * What to scale x and y dimensions by
 *
 * <p>This class is <b>immutable</b>.
 *
 * @author Owen Feehan
 */
@Value
@Accessors(fluent = true)
public final class ScaleFactor {

    /** How much to multiply the existing x-dimension by to create a scaled x-dimension. */
    private final double x;
    
    /** How much to multiply the existing y-dimension by to create a scaled y-dimension. */
    private final double y;

    /**
     * Create with an identical scaling-factor for all dimensions.
     * 
     * @param factor how much to multiply <i>all</i> existing dimensions by to create a scaled dimensions.
     */
    public ScaleFactor(double factor) {
        this(factor, factor);
    }

    /**
     * Create with specific scaling-factors for each dimension.
     * 
     * @param x how much to multiply the existing x-dimension by to create a scaled x-dimension.
     * @param y how much to multiply the existing x-dimension by to create a scaled x-dimension.
     */
    public ScaleFactor(double x, double y) {
        Preconditions.checkArgument(x > 0);
        Preconditions.checkArgument(y > 0);
        this.x = x;
        this.y = y;
    }

    /**
     * Build a new scale factor where each dimension is set to the reciprocal of its current value.
     * 
     * <p>This is an <b>immutable</b> operation.
     * 
     * @return a newly created {@link ScaleFactor} where each component is set to its reciprocal.
     */
    public ScaleFactor invert() {
        return new ScaleFactor(1 / x, 1 / y);
    }

    /**
     * Is the scale-factor identical in x and y dimensions?
     * 
     * @return true if the scale-factor and y are identical within {@code 1e-3} precision.
     */
    public boolean hasIdenticalXY() {
        return Math.abs(x - y) < 1e-3;
    }

    /** 
     * Does the scale-factor involve no scaling at all?
     *
     * @return true if the scale-factor is 1 in all dimensions, false otherwise.
     */
    public boolean isNoScale() {
        return x == 1.0 && y == 1.0;
    }
    
    /**
     * Which is the minimum scaling-factor among all dimensions?
     * 
     * @return the minimum scaling-factor
     */
    public double minimumDimension() {
        return Double.min(x, y);
    }

    @Override
    public String toString() {
        return String.format("x=%f\ty=%f\t\tx^-1=%f\ty^-1=%f", x, y, 1 / x, 1 / y);
    }
}
