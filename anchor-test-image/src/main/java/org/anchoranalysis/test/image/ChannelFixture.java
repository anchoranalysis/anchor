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

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.OptionalFactory;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.factory.ChannelFactory;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsAll;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Creates a {@link Channel} to use in tests.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@AllArgsConstructor
public class ChannelFixture {

    /** Whether to include resolution with the channel's dimensions. */
    private boolean includeResolution = true;

    /** Functional interface for creating intensity values for a given location. */
    @FunctionalInterface
    public interface IntensityFunction {

        /**
         * Calculates the intensity value for a given point.
         *
         * @param point The 3D point.
         * @return The intensity value.
         */
        default int valueFor(Point3i point) {
            return valueFor(point.x(), point.y(), point.z());
        }

        /**
         * Calculates the intensity value for given coordinates.
         *
         * @param x The x-coordinate.
         * @param y The y-coordinate.
         * @param z The z-coordinate.
         * @return The intensity value.
         */
        int valueFor(int x, int y, int z);
    }

    // START: IntensityFunction examples
    /**
     * Calculates intensity as the sum of coordinates modulo 256.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param z The z-coordinate.
     * @return The calculated intensity.
     */
    public static int sumModulo(int x, int y, int z) {
        return modulo(x + y + z);
    }

    /**
     * Calculates intensity as the difference of coordinates modulo 256.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param z The z-coordinate.
     * @return The calculated intensity.
     */
    public static int diffModulo(int x, int y, int z) {
        return modulo(x - y - z);
    }

    /**
     * Calculates intensity as the product of coordinates modulo 256.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param z The z-coordinate.
     * @return The calculated intensity.
     */
    public static int multModulo(int x, int y, int z) {
        return modulo(x * y * z);
    }

    // END: IntensityFunction examples

    // START: image size examples
    /** Small 3D extent for testing. */
    public static final Extent SMALL_3D = new Extent(8, 11, 4);

    /** Small 2D extent for testing. */
    public static final Extent SMALL_2D = SMALL_3D.flattenZ();

    /** Medium 3D extent for testing. */
    public static final Extent MEDIUM_3D = new Extent(69, 61, 7);

    /** Medium 2D extent for testing. */
    public static final Extent MEDIUM_2D = MEDIUM_3D.flattenZ();

    /** Large 3D extent for testing. */
    public static final Extent LARGE_3D = new Extent(1031, 2701, 19);

    /** Large 2D extent for testing. */
    public static final Extent LARGE_2D = LARGE_3D.flattenZ();

    // END: image size examples

    /**
     * Creates a channel with specified extent, intensity function, and voxel data type.
     *
     * @param extent The extent of the channel.
     * @param createIntensity The function to create intensity values.
     * @param channelVoxelType The voxel data type of the channel.
     * @return The created channel.
     */
    public Channel createChannel(
            Extent extent, IntensityFunction createIntensity, VoxelDataType channelVoxelType) {

        Dimensions dimensions = createDimensions(extent);

        Channel channel =
                ChannelFactory.instance().get(channelVoxelType).createEmptyInitialised(dimensions);

        IterateVoxelsAll.withVoxelBuffer(
                channel.voxels().any(),
                (point, buffer, offset) -> buffer.putInt(offset, createIntensity.valueFor(point)));

        return channel;
    }

    /**
     * Creates dimensions from an extent, optionally including resolution.
     *
     * @param extent The extent to use.
     * @return The created dimensions.
     */
    private Dimensions createDimensions(Extent extent) {
        return new Dimensions(
                extent,
                OptionalFactory.create(includeResolution, () -> ResolutionFixture.INSTANCE));
    }

    /**
     * Finds modulus of a number with the maximum byte value (+1)
     *
     * <p>Any negative numbers are treated as zeros.
     *
     * @param number The number to find the modulus of.
     * @return The calculated modulus.
     */
    private static int modulo(int number) {
        if (number >= 0) {
            return Math.floorMod(number, UnsignedByteVoxelType.MAX_VALUE_INT + 1);
        } else {
            return 0;
        }
    }
}
