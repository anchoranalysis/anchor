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

package org.anchoranalysis.anchor.mpp.pxlmark.memo;

import lombok.Getter;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.pxlmark.VoxelizedMark;
import org.anchoranalysis.anchor.mpp.pxlmark.VoxelizedMarkFactory;
import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.error.AnchorNeverOccursException;
import org.anchoranalysis.feature.nrg.NRGStack;

/**
 * Memoization of retrieving a {@VoxelizedMark} from a mark
 *
 * <p>This is avoid repeated expensive operations (rasterization of a mark). TODO switch from
 * inheritance to delegation.
 */
public class VoxelizedMarkMemo {

    // START REQUIRED ARGUMENTS
    private Mark mark;
    private NRGStack stack;

    @Getter private final RegionMap regionMap;
    // END REQUIRED ARGUMENTS

    private CachedOperation<VoxelizedMark, AnchorNeverOccursException> op;

    public VoxelizedMarkMemo(Mark mark, NRGStack stack, RegionMap regionMap) {
        this(mark, stack, regionMap, null);
    }

    public VoxelizedMarkMemo(Mark mark, NRGStack stack, RegionMap regionMap, VoxelizedMark result) {
        this.mark = mark;
        this.stack = stack;
        this.regionMap = regionMap;
        this.op =
                new CachedOperation<VoxelizedMark, AnchorNeverOccursException>(result) {
                    @Override
                    protected VoxelizedMark execute() {
                        return VoxelizedMarkFactory.create(mark, stack, regionMap);
                    }
                };
    }

    // The associated mark
    public Mark getMark() {
        return mark;
    }

    /**
     * A voxelized-respentation of the mark
     *
     * @return
     */
    public VoxelizedMark voxelized() {
        return op.doOperation();
    }

    public void reset() {
        op.reset();
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

    public void cleanUp() {
        VoxelizedMark pm = op.getResult();
        if (pm != null) {
            pm.cleanUp();
        }
    }
}
