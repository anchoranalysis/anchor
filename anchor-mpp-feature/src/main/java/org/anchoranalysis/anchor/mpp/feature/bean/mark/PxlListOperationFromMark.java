/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.bean.mark;

import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.ImageDimensions;

public abstract class PxlListOperationFromMark extends AnchorBean<PxlListOperationFromMark> {

    public abstract double doOperation(VoxelizedMarkMemo pxlMarkMemo, ImageDimensions dimensions)
            throws OperationFailedException;
}
