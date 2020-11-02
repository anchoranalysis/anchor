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
package org.anchoranalysis.test.image.rasterwriter;

import java.io.IOException;
import java.util.Optional;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.test.image.rasterwriter.comparison.ImageComparer;

/**
 * Helper methods to test a {@code RasterWriter} on stacks with between 1 and 4 channels.
 *
 * <p>Both 2D and 3D stacks may be created and tested, depending on parameterization.
 *
 * @author Owen Feehan
 */
public class FourChannelStackTester {

    private static final VoxelDataType[] DEFAULT_VOXEL_TYPE_AS_ARRAY = {
        UnsignedByteVoxelType.INSTANCE
    };

    /** The tester to use for all tests. */
    private final StackTester tester;
    
    /** Comparer to use on non-RGB tests.  */
    private final Optional<ImageComparer> comparer;
    
    /** Comparer to use on RGB tests. */
    private final Optional<ImageComparer> comparerRGB;

    /**
     * Creates for a tester and comparer.
     * 
     * @param tester creates a stack to fulfill certain requirements, and performs the test with it.
     * @param comparer a comparer used on the image-created when tested to check if it is identical to other(s).
     * @param skipComparisonForRGB Iff true, comparisons are not applied to RGB images.
     */
    public FourChannelStackTester(StackTester tester, ImageComparer comparer, boolean skipComparisonForRGB) {
        this.tester = tester;
        this.comparer = Optional.of(comparer);
        this.comparerRGB = OptionalUtilities.createFromFlag(!skipComparisonForRGB, () -> comparer); 
    }
    
    /**
     * Tests the creation of a single-channel stack of unsigned 8-bit data type with the RGB flag
     * off.
     *
     * @throws ImageIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testSingleChannel() throws ImageIOException, IOException {
        testSingleChannel(DEFAULT_VOXEL_TYPE_AS_ARRAY);
    }

    /**
     * Tests the creation of a single-channel stack of specified data type with the RGB flag off.
     *
     * @param channelVoxelType channel voxel-type to use for the test.
     * @throws ImageIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testSingleChannel(VoxelDataType channelVoxelType)
            throws ImageIOException, IOException {
        testSingleChannel(new VoxelDataType[] {channelVoxelType});
    }

    /**
     * Tests the creation of a single-channel stack of specified data types with the RGB flag off.
     *
     * @param channelVoxelTypes creates tests for each {@link VoxelDataType} in the array.
     * @throws ImageIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testSingleChannel(VoxelDataType[] channelVoxelTypes)
            throws ImageIOException, IOException {
        tester.performTest(channelVoxelTypes, 1, false, comparer);
    }

    /**
     * Tests the creation of a single-channel stack of unsigned 8-bit data type with the RGB flag
     * on, which should typically produce an exception.
     *
     * @throws ImageIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testSingleChannelRGB() throws ImageIOException, IOException {
        tester.performTest( new ChannelSpecification(UnsignedByteVoxelType.INSTANCE, 1, true), comparerRGB);
    }

    /**
     * Tests the creation of a two-channel stack of unsigned 8-bit data type.
     *
     * @throws ImageIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testTwoChannels() throws ImageIOException, IOException {
        testTwoChannels(DEFAULT_VOXEL_TYPE_AS_ARRAY);
    }

    /**
     * Tests the creation of a two-channel stack of specified data types.
     *
     * @param channelVoxelTypes creates tests for each {@link VoxelDataType} in the array.
     * @throws ImageIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testTwoChannels(VoxelDataType[] channelVoxelTypes)
            throws ImageIOException, IOException {
        tester.performTest(channelVoxelTypes, 2, false, comparer);
    }

    /**
     * Tests the creation of a three-channel stack of unsigned 8-bit data type with the rgb-flag set
     * to false.
     *
     * @throws ImageIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testThreeChannelsSeparate() throws ImageIOException, IOException {
        testThreeChannelsSeparate(DEFAULT_VOXEL_TYPE_AS_ARRAY);
    }

    /**
     * Tests the creation of a three-channel stack of specified data types with the rgb-flag set to
     * false.
     *
     * @param channelVoxelTypes creates tests for each {@link VoxelDataType} in the array.
     * @throws ImageIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testThreeChannelsSeparate(VoxelDataType[] channelVoxelTypes)
            throws ImageIOException, IOException {
        tester.performTest(channelVoxelTypes, 3, false, comparer);
    }

    /**
     * Tests the creation of a three-channel stack of unsigned 8-bit data type with the rgb-flag set
     * to true.
     *
     * @throws ImageIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testThreeChannelsRGB() throws ImageIOException, IOException {
        testThreeChannelsRGB(DEFAULT_VOXEL_TYPE_AS_ARRAY);
    }

    /**
     * Tests the creation of a three-channel stack of specified data type with the rgb-flag set to
     * true.
     *
     * @param channelVoxelType channel voxel-type to use for the test.
     * @throws ImageIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testThreeChannelsRGB(VoxelDataType channelVoxelType)
            throws ImageIOException, IOException {
        testThreeChannelsRGB(new VoxelDataType[] {channelVoxelType});
    }

    /**
     * Tests the creation of a three-channel stack of specified data types with the rgb-flag set to
     * true.
     *
     * @param channelVoxelTypes creates tests for each {@link VoxelDataType} in the array.
     * @throws ImageIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testThreeChannelsRGB(VoxelDataType[] channelVoxelTypes)
            throws ImageIOException, IOException {
        tester.performTest(channelVoxelTypes, 3, true, comparerRGB);
    }

    /**
     * Tests the creation of a three-channel stack of heterogeneous channel types.
     *
     * <p>The first channel type is {@link UnsignedShortVoxelType} the remaining two are {@link
     * UnsignedByteVoxelType}.
     *
     * @throws ImageIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testThreeChannelsHeterogeneous() throws ImageIOException, IOException {
        tester.performTest(
                new ChannelSpecification(UnsignedByteVoxelType.INSTANCE, 3, false),
                Optional.of(UnsignedShortVoxelType.INSTANCE), comparer);
    }

    /**
     * Tests a stack with four-channels of unsigned 8-bit data type.
     *
     * @throws ImageIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testFourChannels() throws ImageIOException, IOException {
        testFourChannels(DEFAULT_VOXEL_TYPE_AS_ARRAY);
    }

    /**
     * Tests a stack with four-channels of specified types
     *
     * @param channelVoxelTypes creates tests for each {@link VoxelDataType} in the array.
     * @throws ImageIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testFourChannels(VoxelDataType[] channelVoxelTypes)
            throws ImageIOException, IOException {
        tester.performTest(channelVoxelTypes, 4, false, comparer);
    }
}
