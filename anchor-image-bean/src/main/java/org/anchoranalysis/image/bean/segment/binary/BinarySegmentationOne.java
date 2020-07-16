/* (C)2020 */
package org.anchoranalysis.image.bean.segment.binary;

import java.nio.ByteBuffer;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.bean.nonbean.error.SegmentationFailedException;
import org.anchoranalysis.image.bean.nonbean.parameters.BinarySegmentationParameters;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;

public abstract class BinarySegmentationOne extends BinarySegmentation {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private BinarySegmentation sgmn;
    // END BEAN PROPERTIES

    @Override
    public BinaryVoxelBox<ByteBuffer> sgmn(
            VoxelBoxWrapper voxelBox,
            BinarySegmentationParameters params,
            Optional<ObjectMask> mask)
            throws SegmentationFailedException {
        return sgmnFromSgmn(voxelBox, params, mask, sgmn);
    }

    protected abstract BinaryVoxelBox<ByteBuffer> sgmnFromSgmn(
            VoxelBoxWrapper voxelBox,
            BinarySegmentationParameters params,
            Optional<ObjectMask> object,
            BinarySegmentation sgmn)
            throws SegmentationFailedException;
}
