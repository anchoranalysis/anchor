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
 * A bound representing unitless values, where the minimum and maximum are not affected by
 * resolution.
 *
 * <p>This class extends {@link BoundMinMax} to provide functionality for bounds that don't have a
 * specific unit or are not affected by image resolution.
 */
public class BoundUnitless extends BoundMinMax {

    private static final long serialVersionUID = 8738881592311859713L;

    /** Creates a new instance with default minimum and maximum values. */
    public BoundUnitless() {
        super();
    }

    /**
     * Creates a new instance with specified minimum and maximum values.
     *
     * @param min the minimum value
     * @param max the maximum value
     */
    public BoundUnitless(double min, double max) {
        super(min, max);
    }

    /**
     * Creates a new instance by copying another BoundUnitless.
     *
     * @param src the source BoundUnitless to copy from
     */
    public BoundUnitless(BoundUnitless src) {
        super(src);
    }

    @Override
    public String describeBean() {
        return String.format("%s(min=%f,max=%f)", getBeanName(), getMin(), getMax());
    }

    @Override
    public double getMinResolved(Optional<Resolution> resolution, boolean do3D) {
        return getMin();
    }

    @Override
    public double getMaxResolved(Optional<Resolution> resolution, boolean do3D) {
        return getMax();
    }

    /**
     * Calculates the size of the bound range.
     *
     * @return the size of the range (max - min + 1)
     */
    public double size() {
        return getMax() - getMin() + 1;
    }

    @Override
    public Bound duplicate() {
        return new BoundUnitless(this);
    }
}
