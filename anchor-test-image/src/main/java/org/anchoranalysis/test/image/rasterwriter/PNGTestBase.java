package org.anchoranalysis.test.image.rasterwriter;

import java.io.IOException;
import java.util.Optional;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterwriter.RasterWriter;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.junit.Test;

/**
 * For testing all {@link RasterWriter}s that create PNGs.
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
 * @author Owen Feehan
 */
public abstract class PNGTestBase extends RasterWriterTestBase {

    /** All possible voxel types that can be supported. */
    protected static final VoxelDataType[] ALL_SUPPORTED_VOXEL_TYPES = {
        UnsignedByteVoxelType.INSTANCE, UnsignedShortVoxelType.INSTANCE
    };

    public PNGTestBase() {
        super("png", false, true, Optional.of("ome.xml"));
    }

    @Test
    public void testSingleChannel() throws RasterIOException, IOException {
        tester.testSingleChannel(ALL_SUPPORTED_VOXEL_TYPES);
    }

    @Test(expected = RasterIOException.class)
    public void testSingleChannelInt() throws RasterIOException, IOException {
        tester.testSingleChannel(UnsignedIntVoxelType.INSTANCE);
    }

    @Test(expected = RasterIOException.class)
    public void testSingleChannelRGB() throws RasterIOException, IOException {
        tester.testSingleChannelRGB();
    }

    @Test(expected = RasterIOException.class)
    public void testTwoChannels() throws RasterIOException, IOException {
        tester.testTwoChannels();
    }

    @Test(expected = RasterIOException.class)
    public void testThreeChannelsSeparate() throws RasterIOException, IOException {
        tester.testThreeChannelsSeparate();
    }

    @Test
    public void testThreeChannelsRGBUnsignedByte() throws RasterIOException, IOException {
        tester.testThreeChannelsRGB(UnsignedByteVoxelType.INSTANCE);
    }

    @Test(expected = RasterIOException.class)
    public void testFourChannels() throws RasterIOException, IOException {
        tester.testFourChannels();
    }
}
