/*-
 * #%L
 * anchor-mpp-feature
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

package org.anchoranalysis.mpp.feature.energy.saved;

import java.io.Serializable;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParameters;
import org.anchoranalysis.mpp.feature.energy.scheme.EnergySchemeWithSharedFeatures;
import org.anchoranalysis.mpp.feature.mark.EnergyMemoList;

/**
 * Saves and manages the total energy for all items in a collection.
 *
 * <p>This class implements {@link Serializable} for persistence of energy calculations.
 */
public class EnergySavedAll implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The total energy for all items. */
    private double energyTotal;

    /**
     * Calculates the total energy for all items using the provided energy scheme and stack.
     *
     * @param memoList the {@link EnergyMemoList} containing energy memos for marks
     * @param energyScheme the {@link EnergySchemeWithSharedFeatures} used for energy calculations
     * @param energyStack the {@link EnergyStackWithoutParameters} providing context for energy
     *     calculations
     * @throws NamedFeatureCalculateException if there's an error during energy calculation
     */
    public void calc(
            EnergyMemoList memoList,
            EnergySchemeWithSharedFeatures energyScheme,
            EnergyStackWithoutParameters energyStack)
            throws NamedFeatureCalculateException {
        energyTotal = energyScheme.totalAll(memoList, energyStack).getTotal();
    }

    /**
     * Gets the total energy for all items.
     *
     * @return the total energy as a double
     */
    public double getEnergyTotal() {
        return energyTotal;
    }

    /**
     * Creates a shallow copy of this instance.
     *
     * @return a new {@link EnergySavedAll} with the same total energy
     */
    public EnergySavedAll shallowCopy() {
        EnergySavedAll out = new EnergySavedAll();
        out.energyTotal = energyTotal;
        return out;
    }

    /**
     * Creates a deep copy of this instance.
     *
     * @return a new {@link EnergySavedAll} with the same total energy
     */
    public EnergySavedAll deepCopy() {
        return shallowCopy();
    }
}
