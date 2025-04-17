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

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.random.RandomNumberGenerator;

/**
 * A bound resolved into pixel units.
 *
 * <p>This class represents a bound with minimum and maximum values, typically used after resolving
 * a bound into pixel units.
 */
@NoArgsConstructor
@AllArgsConstructor
public class ResolvedBound extends AnchorBean<ResolvedBound> implements Serializable {

    private static final long serialVersionUID = 1L;

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private double min = 0.0;

    @BeanField @Getter @Setter private double max = 1.0;
    // END BEAN PROPERTIES

    /**
     * Creates a new instance by copying another ResolvedBound.
     *
     * @param src the source ResolvedBound to copy from
     */
    public ResolvedBound(ResolvedBound src) {
        this.min = src.min;
        this.max = src.max;
    }

    /**
     * Checks if a value is contained within the bound.
     *
     * @param val the value to check
     * @return true if the value is within the bound, false otherwise
     */
    public boolean contains(double val) {
        return (val >= min && val <= max);
    }

    /**
     * Gets a description of the bound.
     *
     * @return a string description of the bound
     */
    public String getDscr() {
        return String.format("resolvedBound(min=%f,max=%f)", getMin(), getMax());
    }

    /**
     * Scales the bound by a multiplication factor.
     *
     * @param multFactor the factor to multiply the bound values by
     */
    public void scale(double multFactor) {
        this.min = this.min * multFactor;
        this.max = this.max * multFactor;
    }

    /**
     * Generates a random value between the bounds (open interval).
     *
     * @param randomNumberGenerator the random number generator to use
     * @return a random value between min and max
     */
    public double randOpen(RandomNumberGenerator randomNumberGenerator) {
        return randomNumberGenerator.sampleDoubleFromRange(min, max);
    }

    @Override
    public String toString() {
        return String.format("%f-%f", min, max);
    }
}
