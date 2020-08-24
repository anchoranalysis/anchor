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

package org.anchoranalysis.anchor.mpp.feature.bean.mark;

import org.anchoranalysis.anchor.mpp.bean.MPPBean;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.feature.error.CheckException;
import org.anchoranalysis.anchor.mpp.mark.CompatibleWithMark;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.energy.EnergyStack;

/**
 * A predicate on a mark to check if a condition has been satisfied.
 * 
 * @author Owen Feehan
 *
 */
public abstract class CheckMark extends MPPBean<CheckMark> implements CompatibleWithMark {

    /**
     * Called before any calls to {@link #check}
     *
     * @param energyStack
     */
    public void start(EnergyStack energyStack) throws OperationFailedException {}

    /**
     * Checks a mark
     *
     * @param mark the mark to check
     * @param regionMap the region-map
     * @param energyStack the corresponding energy-stack for the mark
     * @return true if the mark satisfies the <i>check</i>, false otherwise
     */
    public abstract boolean check(Mark mark, RegionMap regionMap, EnergyStack energyStack)
            throws CheckException;

    /** Called after any calls to {@link #check} */
    public void end() {}
}
