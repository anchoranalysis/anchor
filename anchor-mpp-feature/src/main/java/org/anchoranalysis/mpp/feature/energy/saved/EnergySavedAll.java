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

// Saves particular features for all items
public class EnergySavedAll implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    private double energyTotal;

    public void calc(
            EnergyMemoList pxlMarkMemoList,
            EnergySchemeWithSharedFeatures energyScheme,
            EnergyStackWithoutParameters energyStack)
            throws NamedFeatureCalculateException {
        energyTotal = energyScheme.totalAll(pxlMarkMemoList, energyStack).getTotal();
    }

    public double getEnergyTotal() {
        return energyTotal;
    }

    public EnergySavedAll shallowCopy() {
        EnergySavedAll out = new EnergySavedAll();
        out.energyTotal = energyTotal;
        return out;
    }

    public EnergySavedAll deepCopy() {
        return shallowCopy();
    }
}
