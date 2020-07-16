/* (C)2020 */
package org.anchoranalysis.image.chnl;

import static org.junit.Assert.*;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactoryFloat;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferFloat;
import org.junit.Test;

public class ChnlIPTest {

    @Test
    public void testSetPixelsForPlane() {

        ChannelFactorySingleType imgChnlFloatFactory = new ChannelFactoryFloat();

        ImageDimensions sd = new ImageDimensions(new Extent(2, 2, 1));

        Channel ic = imgChnlFloatFactory.createEmptyInitialised(sd);

        float[] arr = new float[] {1, 2, 3, 4};

        VoxelBox<FloatBuffer> vb = ic.getVoxelBox().asFloat();
        vb.getPlaneAccess().setPixelsForPlane(0, VoxelBufferFloat.wrap(arr));

        double delta = 1e-3;
        assertEquals(1.0f, vb.getVoxel(0, 0, 0), delta);
        assertEquals(2.0f, vb.getVoxel(1, 0, 0), delta);
        assertEquals(3.0f, vb.getVoxel(0, 1, 0), delta);
        assertEquals(4.0f, vb.getVoxel(1, 1, 0), delta);
    }
}
