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

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;

/**
 * An abstract class representing a bound with minimum and maximum values.
 *
 * <p>This class extends {@link Bound} and uses a {@link ResolvedBound} as a delegate to manage the
 * minimum and maximum values.
 */
public abstract class BoundMinMax extends Bound {

    private static final long serialVersionUID = -493791653577617743L;

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private ResolvedBound delegate;

    // END BEAN PROPERTIES

    /** Creates a new instance with default minimum and maximum values. */
    protected BoundMinMax() {
        delegate = new ResolvedBound();
    }

    /**
     * Creates a new instance with specified minimum and maximum values.
     *
     * @param min the minimum value
     * @param max the maximum value
     */
    protected BoundMinMax(double min, double max) {
        delegate = new ResolvedBound(min, max);
    }

    /**
     * Creates a new instance by copying another BoundMinMax.
     *
     * @param source the source BoundMinMax to copy from
     */
    protected BoundMinMax(BoundMinMax source) {
        delegate = new ResolvedBound(source.delegate);
    }

    /**
     * Gets the minimum value of the bound.
     *
     * @return the minimum value
     */
    public double getMin() {
        return delegate.getMin();
    }

    /**
     * Sets the minimum value of the bound.
     *
     * @param min the minimum value to set
     */
    public void setMin(double min) {
        delegate.setMin(min);
    }

    /**
     * Gets the maximum value of the bound.
     *
     * @return the maximum value
     */
    public double getMax() {
        return delegate.getMax();
    }

    /**
     * Sets the maximum value of the bound.
     *
     * @param max the maximum value to set
     */
    public void setMax(double max) {
        delegate.setMax(max);
    }

    @Override
    public String describeBean() {
        return delegate.getDscr();
    }

    @Override
    public void scale(double multFactor) {
        delegate.scale(multFactor);
    }
}
