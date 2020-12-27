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
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.writer.StackWriter;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.test.image.rasterwriter.comparison.ComparisonPlan;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * For testing all {@link StackWriter}s that create TIFFs.
 *
 * <p>Note that {@link ComparisonPlan#ComparisonPlan(boolean, Optional, boolean, String)} can be
 * used to quickly created the saved copies in the resources.
 *
 * @author Owen Feehan
 */
public abstract class TiffTestBase extends RasterWriterTestBase {

    private static final ComparisonPlan COMPARISON_PLAN =
            new ComparisonPlan(true, Optional.of(ImageFileFormat.OME_TIFF), false);

    private static final VoxelDataType[] SUPPORTED_VOXEL_TYPES =
            RasterWriterTestBase.ALL_SUPPORTED_VOXEL_TYPES;

    protected TiffTestBase() {
        super(ImageFileFormat.TIFF, true, COMPARISON_PLAN);
    }

    @Test
    void testSingleChannel() throws ImageIOException, IOException {
        tester.testSingleChannel(SUPPORTED_VOXEL_TYPES);
    }

    void testSingleChannelRGB() throws ImageIOException, IOException {
        tester.testSingleChannelRGB();
    }

    @Test
    void testTwoChannels() {
        assertThrows(ImageIOException.class, () ->
            tester.testTwoChannels()
        );
    }

    @Test
    void testThreeChannelsSeparate() throws ImageIOException, IOException {
        tester.testThreeChannelsSeparate(SUPPORTED_VOXEL_TYPES);
    }

    @Test
    void testThreeChannelsRGB() throws ImageIOException, IOException {
        tester.testThreeChannelsRGB();
    }

    @Test
    void testThreeChannelsRGBUnsignedShort() throws ImageIOException, IOException {
        tester.testThreeChannelsRGB(UnsignedShortVoxelType.INSTANCE);
    }

    @Test
    void testFourChannels() {
        assertThrows(ImageIOException.class, () ->
            tester.testFourChannels()
        );
    }
}
