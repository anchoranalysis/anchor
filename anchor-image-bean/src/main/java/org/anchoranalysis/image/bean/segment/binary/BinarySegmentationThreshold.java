/* (C)2020 */
package org.anchoranalysis.image.bean.segment.binary;

import java.nio.ByteBuffer;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.bean.nonbean.error.SegmentationFailedException;
import org.anchoranalysis.image.bean.nonbean.parameters.BinarySegmentationParameters;
import org.anchoranalysis.image.bean.threshold.Thresholder;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;

public class BinarySegmentationThreshold extends BinarySegmentation {

    // START PARAMETERS
    @BeanField @Getter @Setter private Thresholder thresholder;
    // END PARAMETERS

    @Override
    public BinaryVoxelBox<ByteBuffer> sgmn(
            VoxelBoxWrapper voxelBox,
            BinarySegmentationParameters params,
            Optional<ObjectMask> mask)
            throws SegmentationFailedException {
        try {
            return thresholder.threshold(
                    voxelBox,
                    BinaryValuesByte.getDefault(),
                    mask.isPresent() ? Optional.empty() : params.getIntensityHistogram(),
                    mask);
        } catch (OperationFailedException e) {
            throw new SegmentationFailedException(e);
        }
    }
}
