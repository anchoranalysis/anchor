/* (C)2020 */
package org.anchoranalysis.anchor.mpp.pxlmark.memo;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.feature.nrg.NRGStack;

// We more explicitly manage the creation and deletiion of PxlMarkMemo as they can take up a large
// amount of memory
public class PxlMarkMemoFactory {

    private PxlMarkMemoFactory() {
        // ONLY ALLOWED AS STATIC
    }

    public static VoxelizedMarkMemo create(Mark mark, NRGStack stack, RegionMap regionMap) {
        return new VoxelizedMarkMemo(mark, stack, regionMap);
    }
}
