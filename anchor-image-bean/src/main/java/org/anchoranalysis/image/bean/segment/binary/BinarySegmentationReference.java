/* (C)2020 */
package org.anchoranalysis.image.bean.segment.binary;

import java.nio.ByteBuffer;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.image.bean.nonbean.error.SegmentationFailedException;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.bean.nonbean.parameters.BinarySegmentationParameters;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;

public class BinarySegmentationReference extends BinarySegmentation {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String id;
    // END BEAN PROPERTIES

    private BinarySegmentation proxy;

    @Override
    public void onInit(ImageInitParams so) throws InitException {
        super.onInit(so);
        try {
            proxy = getInitializationParameters().getBinarySgmnSet().getException(id);
        } catch (NamedProviderGetException e) {
            throw new InitException(e.summarize());
        }
    }

    @Override
    public BinaryVoxelBox<ByteBuffer> sgmn(
            VoxelBoxWrapper voxelBox,
            BinarySegmentationParameters params,
            Optional<ObjectMask> mask)
            throws SegmentationFailedException {
        return proxy.sgmn(voxelBox, params, mask);
    }
}
