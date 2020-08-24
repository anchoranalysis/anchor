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

package org.anchoranalysis.anchor.mpp.mark.voxelized.memo;

import lombok.Getter;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.voxelized.VoxelizedMark;
import org.anchoranalysis.anchor.mpp.mark.voxelized.VoxelizedMarkFactory;
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.error.AnchorNeverOccursException;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParams;

/** Memoization of retrieving a {@link VoxelizedMark} from a mark */
public class VoxelizedMarkMemo {

    // START REQUIRED ARGUMENTS
    /** The associated mark */
    @Getter private Mark mark;

    private EnergyStackWithoutParams stack;

    @Getter private final RegionMap regionMap;
    // END REQUIRED ARGUMENTS

    private CachedSupplier<VoxelizedMark, AnchorNeverOccursException> cachedMark;

    public VoxelizedMarkMemo(Mark mark, EnergyStackWithoutParams stack, RegionMap regionMap) {
        this.mark = mark;
        this.stack = stack;
        this.regionMap = regionMap;
        this.cachedMark =
                CachedSupplier.cache(() -> VoxelizedMarkFactory.create(mark, stack, regionMap));
    }

    /**
     * A voxelized-respentation of the mark
     *
     * @return
     */
    public VoxelizedMark voxelized() {
        return cachedMark.get();
    }

    public void reset() {
        cachedMark.reset();
    }

    /**
     * Assigns a new mark to replace the existing mark.
     *
     * @param mark
     */
    public void assignFrom(Mark mark) {
        this.mark = mark;
        reset();
    }

    // Duplicates the current mark memo, resetting the calculation state
    public VoxelizedMarkMemo duplicateFresh() {
        return PxlMarkMemoFactory.create(this.mark.duplicate(), stack, regionMap);
    }
}
