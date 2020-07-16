/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark;

import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.GenerateUniqueParameterization;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.statistics.VoxelStatistics;

public abstract class MarkRegion extends AnchorBean<MarkRegion>
        implements GenerateUniqueParameterization {

    public abstract VoxelStatistics createStatisticsFor(
            VoxelizedMarkMemo memo, ImageDimensions dimensions) throws CreateException;
}
