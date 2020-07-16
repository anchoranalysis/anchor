/* (C)2020 */
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
