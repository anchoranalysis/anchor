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
package org.anchoranalysis.test.image.stackwriter;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Optional;
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.writer.StackWriter;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.test.image.rasterwriter.comparison.ComparisonPlan;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * For testing all {@link StackWriter}s that create PNGs.
 *
 * <p>It assumes;
 *
 * <ul>
 *   <li>8-bit and 16-bit grayscale is supported
 *   <li>8-bit RGB is supported
 * </ul>
 *
 * And no other formats are supported.
 *
 * <p>Note that {@link ComparisonPlan#ComparisonPlan(boolean, Optional, boolean, String)} can be
 * used to quickly created the saved copies in the resources.
 *
 * @author Owen Feehan
 */
public abstract class PNGTestBase extends StackWriterTestBase {

    private static final ComparisonPlan COMPARISON_PLAN =
            new ComparisonPlan(false, Optional.of(ImageFileFormat.OME_TIFF), false);

    /** All possible voxel types that can be supported. */
    protected static final VoxelDataType[] ALL_SUPPORTED_VOXEL_TYPES = {
        UnsignedByteVoxelType.INSTANCE, UnsignedShortVoxelType.INSTANCE
    };

    protected PNGTestBase() {
        super(ImageFileFormat.PNG, false, COMPARISON_PLAN);
    }

    @Test
    void testSingleChannel() throws ImageIOException, IOException {
        tester.testSingleChannel(ALL_SUPPORTED_VOXEL_TYPES);
    }

    @Test
    void testSingleChannelInt() {
        assertException(() -> tester.testSingleChannel(UnsignedIntVoxelType.INSTANCE));
    }

    void testSingleChannelRGB() throws ImageIOException, IOException {
        tester.testSingleChannelRGB();
    }

    @Test
    void testTwoChannels() {
        assertException(() -> tester.testTwoChannels());
    }

    @Test
    void testThreeChannelsSeparate() {
        assertException(() -> tester.testThreeChannelsSeparate());
    }

    @Test
    void testThreeChannelsRGBUnsignedByte() throws ImageIOException, IOException {
        tester.testThreeChannelsRGB(UnsignedByteVoxelType.INSTANCE);
    }

    @Test
    void testFourChannels() {
        assertException(() -> tester.testFourChannels());
    }

    @Test
    void testThreeChannelsRGBUnsignedShort() {
        assertException(() -> tester.testThreeChannelsRGB(UnsignedShortVoxelType.INSTANCE));
    }

    private void assertException(Executable executable) {
        assertThrows(ImageIOException.class, executable);
    }
}
