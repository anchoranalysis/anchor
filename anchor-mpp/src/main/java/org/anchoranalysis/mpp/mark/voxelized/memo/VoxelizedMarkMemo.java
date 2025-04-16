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

package org.anchoranalysis.mpp.mark.voxelized.memo;

import lombok.Getter;
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.exception.AnchorNeverOccursException;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParameters;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.voxelized.VoxelizedMark;
import org.anchoranalysis.mpp.mark.voxelized.VoxelizedMarkFactory;

/**
 * Memoization of retrieving a {@link VoxelizedMark} from a mark.
 * <p>
 * This class caches the voxelized representation of a mark to avoid repeated calculations.
 * </p>
 */
public class VoxelizedMarkMemo {

    /** The associated mark. */
    @Getter private Mark mark;

    /** The energy stack without parameters. */
    private EnergyStackWithoutParameters stack;

    /** The region map. */
    @Getter private final RegionMap regionMap;

    /** Cached supplier for the voxelized mark. */
    private CachedSupplier<VoxelizedMark, AnchorNeverOccursException> cachedMark;

    /**
     * Creates a new VoxelizedMarkMemo.
     *
     * @param mark the mark to be voxelized
     * @param stack the energy stack without parameters
     * @param regionMap the region map
     */
    public VoxelizedMarkMemo(Mark mark, EnergyStackWithoutParameters stack, RegionMap regionMap) {
        this.mark = mark;
        this.stack = stack;
        this.regionMap = regionMap;
        this.cachedMark =
                CachedSupplier.cacheChecked(
                        () -> VoxelizedMarkFactory.create(mark, stack, regionMap));
    }

    /**
     * Gets the voxelized representation of the mark.
     *
     * @return the voxelized mark
     */
    public VoxelizedMark voxelized() {
        return cachedMark.get();
    }

    /**
     * Resets the cached voxelized mark.
     */
    public void reset() {
        cachedMark.reset();
    }

    /**
     * Assigns a new mark to replace the existing mark.
     *
     * @param mark the new mark to assign
     */
    public void assignFrom(Mark mark) {
        this.mark = mark;
        reset();
    }

    /**
     * Duplicates the current mark memo, resetting the calculation state.
     *
     * @return a new VoxelizedMarkMemo with the same mark, stack, and region map
     */
    public VoxelizedMarkMemo duplicateFresh() {
        return VoxelizedMarkMemoFactory.create(this.mark.duplicate(), stack, regionMap);
    }
}