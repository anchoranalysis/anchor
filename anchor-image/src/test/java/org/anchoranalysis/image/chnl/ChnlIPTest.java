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
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactoryFloat;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferFloat;
import org.anchoranalysis.image.voxel.extracter.VoxelsExtracter;
import org.junit.Test;

public class ChnlIPTest {

    double delta = 1e-3;

    @Test
    public void testSetPixelsForPlane() {

        ChannelFactorySingleType imgChnlFloatFactory = new ChannelFactoryFloat();

        ImageDimensions dimensions = new ImageDimensions(2, 2, 1);

        Channel channel = imgChnlFloatFactory.createEmptyInitialised(dimensions);

        Voxels<FloatBuffer> voxels = channel.voxels().asFloat();
        voxels.slices().replaceSlice(0, VoxelBufferFloat.wrap(new float[] {1, 2, 3, 4}));

        VoxelsExtracter<FloatBuffer> extracter = voxels.extract();
        assertVoxelEquals(1.0f, 0, 0, extracter);
        assertVoxelEquals(2.0f, 1, 0, extracter);
        assertVoxelEquals(3.0f, 0, 1, extracter);
        assertVoxelEquals(4.0f, 1, 1, extracter);
    }

    private void assertVoxelEquals(
            double value, int x, int y, VoxelsExtracter<FloatBuffer> extracter) {
        assertEquals(value, extracter.voxel(new Point3i(x, y, 0)), delta);
    }
}
