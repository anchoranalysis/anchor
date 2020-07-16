/* (C)2020 */
package org.anchoranalysis.image.bean.segment.binary;

import java.nio.ByteBuffer;
import java.util.Optional;
import org.anchoranalysis.bean.annotation.GroupingRoot;
import org.anchoranalysis.image.bean.nonbean.error.SegmentationFailedException;
import org.anchoranalysis.image.bean.nonbean.parameters.BinarySegmentationParameters;
import org.anchoranalysis.image.bean.segment.SegmentationBean;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;

@GroupingRoot
public abstract class BinarySegmentation extends SegmentationBean<BinarySegmentation> {

    // Returns a BinaryVoxelBox associated with the input buffer or perhaps an newly created buffer
    // of identical size
    public abstract BinaryVoxelBox<ByteBuffer> sgmn(
            VoxelBoxWrapper voxelBox,
            BinarySegmentationParameters params,
            Optional<ObjectMask> mask)
            throws SegmentationFailedException;
}
