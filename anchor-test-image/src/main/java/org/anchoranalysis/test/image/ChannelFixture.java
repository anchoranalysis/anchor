/*-
 * #%L
 * anchor-test-image
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

package org.anchoranalysis.test.image;

import java.nio.Buffer;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;

public class ChannelFixture {

    // Creates an intensity value for a given location
    @FunctionalInterface
    public interface IntensityFunction {
        int valueFor(int x, int y, int z);
    }

    // START: IntensityFunction examples
    public static int sumMod(int x, int y, int z) {
        return mod(x + y + z);
    }

    public static int diffMod(int x, int y, int z) {
        return mod(x - y - z);
    }

    public static int multMod(int x, int y, int z) {
        int xy = mod(x * y);
        return mod(xy * z);
    }
    // END: IntensityFunction examples

    // START: image size examples
    public static final Extent SMALL_3D = new Extent(8, 11, 4);
    public static final Extent SMALL_2D = SMALL_3D.flattenZ();
    public static final Extent MEDIUM_3D = new Extent(69, 61, 7);
    public static final Extent MEDIUM_2D = MEDIUM_3D.flattenZ();
    public static final Extent LARGE_3D = new Extent(1031, 2701, 19);
    public static final Extent LARGE_2D = LARGE_3D.flattenZ();
    // END: image size examples

    public static Channel createChannel(Extent e, IntensityFunction createIntensity) {

        ImageDimensions dimensions = new ImageDimensions(e, ImageResFixture.INSTANCE);

        Channel channel = new ChannelFactoryByte().createEmptyInitialised(dimensions);

        // Populate the channel with values
        for (int z = 0; z < e.z(); z++) {

            VoxelBuffer<? extends Buffer> slice = channel.voxels().slice(z);

            for (int x = 0; x < e.x(); x++) {
                for (int y = 0; y < e.y(); y++) {
                    int intens = createIntensity.valueFor(x, y, z);
                    slice.putInt(e.offset(x, y), intens);
                }
            }
        }

        return channel;
    }

    // Finds modulus of a number with the maximum byte value (+1)
    private static int mod(int num) {
        return Math.floorMod(num, VoxelDataTypeUnsignedByte.MAX_VALUE_INT + 1);
    }
}