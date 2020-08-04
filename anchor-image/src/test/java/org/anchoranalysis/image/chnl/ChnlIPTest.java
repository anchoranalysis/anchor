/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.chnl;

import static org.junit.Assert.*;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactoryFloat;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferFloat;
import org.junit.Test;

public class ChnlIPTest {

    @Test
    public void testSetPixelsForPlane() {

        ChannelFactorySingleType imgChnlFloatFactory = new ChannelFactoryFloat();

        ImageDimensions dimensions = new ImageDimensions(2, 2, 1);

        Channel channel = imgChnlFloatFactory.createEmptyInitialised(dimensions);

        Voxels<FloatBuffer> voxels = channel.voxels().asFloat();
        voxels.getPlaneAccess().setPixelsForPlane(0, VoxelBufferFloat.wrap(new float[] {1, 2, 3, 4}));

        double delta = 1e-3;
        assertEquals(1.0f, voxels.getVoxel(0, 0, 0), delta);
        assertEquals(2.0f, voxels.getVoxel(1, 0, 0), delta);
        assertEquals(3.0f, voxels.getVoxel(0, 1, 0), delta);
        assertEquals(4.0f, voxels.getVoxel(1, 1, 0), delta);
    }
}
