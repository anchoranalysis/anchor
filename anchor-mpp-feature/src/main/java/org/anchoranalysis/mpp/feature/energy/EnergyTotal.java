/*-
 * #%L
 * anchor-feature
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

package org.anchoranalysis.mpp.feature.energy;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * Represents the total energy of a system, which can be modified and queried.
 *
 * <p>This class is serializable and provides methods for deep copying, adding energy, and
 * retrieving the total energy.
 */
@AllArgsConstructor
@RequiredArgsConstructor
public class EnergyTotal implements Serializable {

    /** Serialization version UID. */
    private static final long serialVersionUID = -595099053761257083L;

    /** The total energy value. */
    private double total = 0;

    /**
     * Creates a deep copy of this {@link EnergyTotal} instance.
     *
     * @return a new {@link EnergyTotal} instance with the same total energy value
     */
    public EnergyTotal deepCopy() {
        EnergyTotal out = new EnergyTotal();
        out.total = total;
        return out;
    }

    /**
     * Adds a value to the total energy.
     *
     * @param val the value to add to the total energy
     */
    public final void add(double val) {
        total += val;
    }

    /**
     * Gets the current total energy value.
     *
     * @return the total energy as a double
     */
    public final double getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return String.format("%8.3f", this.total);
    }
}
