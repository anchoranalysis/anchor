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
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.stack.StackWriter;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.junit.Test;

/**
 * For testing all {@link StackWriter}s that create TIFFs.
 *
 * @author Owen Feehan
 */
public abstract class TiffTestBase extends RasterWriterTestBase {

    private static final VoxelDataType[] SUPPORTED_VOXEL_TYPES =
            RasterWriterTestBase.ALL_SUPPORTED_VOXEL_TYPES;

    public TiffTestBase() {
        super("tif", true, true, Optional.of("ome.xml"));
    }

    @Test
    public void testSingleChannel() throws RasterIOException, IOException {
        tester.testSingleChannel(SUPPORTED_VOXEL_TYPES);
    }

    @Test(expected = RasterIOException.class)
    public void testSingleChannelRGB() throws RasterIOException, IOException {
        tester.testSingleChannelRGB();
    }

    @Test(expected = RasterIOException.class)
    public void testTwoChannels() throws RasterIOException, IOException {
        tester.testTwoChannels();
    }

    @Test
    public void testThreeChannelsSeparate() throws RasterIOException, IOException {
        tester.testThreeChannelsSeparate(SUPPORTED_VOXEL_TYPES);
    }

    @Test
    public void testThreeChannelsRGB() throws RasterIOException, IOException {
        tester.testThreeChannelsRGB();
    }

    @Test
    public void testThreeChannelsRGBUnsignedShort() throws RasterIOException, IOException {
        tester.testThreeChannelsRGB(UnsignedShortVoxelType.INSTANCE);
    }

    @Test(expected = RasterIOException.class)
    public void testFourChannels() throws RasterIOException, IOException {
        tester.testFourChannels();
    }
}
