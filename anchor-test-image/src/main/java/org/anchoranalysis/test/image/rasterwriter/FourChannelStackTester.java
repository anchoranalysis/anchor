package org.anchoranalysis.test.image.rasterwriter;

import java.io.IOException;
import java.nio.file.Path;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterwriter.RasterWriter;
import org.anchoranalysis.image.io.rasterwriter.RasterWriteOptions;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.test.image.ChannelFixture;
import org.anchoranalysis.test.image.StackFixture;
import lombok.AllArgsConstructor;

/**
 * Helper methods to test a {@code RasterWriter} on stacks with between 1 and 4 channels.
 * 
 * <p>Both 2D and 3D stacks may be created and tested, depending on parameterization.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public class FourChannelStackTester {
    
    private static final String EXTENT_IDENTIFIER = "small";
    
    private static final VoxelDataType[] DEFAULT_VOXEL_TYPE_AS_ARRAY = { UnsignedByteVoxelType.INSTANCE };
    
    // START REQUIRED ARGUMENTS
    /** The writer to use for creating new raster-files that are tested for bytewise equality against saved rasters. */
    private final RasterWriter writer;
    
    /** The directory to write new files to. */
    private final Path directoryToWriteTo;
    
    /** How to compare stacks by two different methods. */
    private final DualStackComparer comparer;
    
    /** If true, then 3D stacks are also tested and saved, not just 2D stacks. */
    private final boolean include3D;
    // END REQUIRED ARGUMENTS
    
    /**
     * Tests the creation of a single-channel stack of unsigned 8-bit data type with the RGB flag off.
     * 
     * @throws RasterIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testSingleChannel() throws RasterIOException, IOException {
        testSingleChannel(DEFAULT_VOXEL_TYPE_AS_ARRAY);
    }
    
    /**
     * Tests the creation of a single-channel stack of specified data type with the RGB flag off.
     * 
     * @param channelVoxelType channel voxel-type to use for the test.
     * @throws RasterIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testSingleChannel(VoxelDataType channelVoxelType) throws RasterIOException, IOException {
        testSingleChannel( new VoxelDataType[]{channelVoxelType} );
    }
    
    /**
     * Tests the creation of a single-channel stack of specified data types with the RGB flag off.
     * 
     * @param channelVoxelTypes creates tests for each {@link VoxelDataType} in the array.
     * @throws RasterIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testSingleChannel(VoxelDataType[] channelVoxelTypes) throws RasterIOException, IOException {
        performTest(channelVoxelTypes, 1, false);
    }
    
    /**
     * Tests the creation of a single-channel stack of unsigned 8-bit data type with the RGB flag on, which should typically produce an exception.
     * 
     * @throws RasterIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testSingleChannelRGB() throws RasterIOException, IOException {
        performTest(UnsignedByteVoxelType.INSTANCE, 1, true);
    }
    
    /**
     * Tests the creation of a two-channel stack of unsigned 8-bit data type.
     * 
     * @throws RasterIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testTwoChannels() throws RasterIOException, IOException {
        testTwoChannels(DEFAULT_VOXEL_TYPE_AS_ARRAY);
    }
    
    /**
     * Tests the creation of a two-channel stack of specified data types.
     * 
     * @param channelVoxelTypes creates tests for each {@link VoxelDataType} in the array.
     * @throws RasterIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testTwoChannels(VoxelDataType[] channelVoxelTypes) throws RasterIOException, IOException {
        performTest(channelVoxelTypes, 2, false);
    }

    /**
     * Tests the creation of a three-channel stack of unsigned 8-bit data type with the rgb-flag set to false.
     * 
     * @throws RasterIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testThreeChannelsSeparate() throws RasterIOException, IOException {
        testThreeChannelsSeparate(DEFAULT_VOXEL_TYPE_AS_ARRAY);
    }
    
    /**
     * Tests the creation of a three-channel stack of specified data types with the rgb-flag set to false.
     * 
     * @param channelVoxelTypes creates tests for each {@link VoxelDataType} in the array.
     * @throws RasterIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testThreeChannelsSeparate(VoxelDataType[] channelVoxelTypes) throws RasterIOException, IOException {
        performTest(channelVoxelTypes, 3, false);
    }

    /**
     * Tests the creation of a three-channel stack of unsigned 8-bit data type with the rgb-flag set to true.
     * 
     * @throws RasterIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testThreeChannelsRGB() throws RasterIOException, IOException {
        testThreeChannelsRGB(DEFAULT_VOXEL_TYPE_AS_ARRAY);
    }
    
    /**
     * Tests the creation of a three-channel stack of specified data type with the rgb-flag set to true.
     * 
     * @param channelVoxelType channel voxel-type to use for the test.
     * @throws RasterIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testThreeChannelsRGB(VoxelDataType channelVoxelType) throws RasterIOException, IOException {
        testThreeChannelsRGB( new VoxelDataType[]{channelVoxelType});
    }
    
    /**
     * Tests the creation of a three-channel stack of specified data types with the rgb-flag set to true.
     * 
     * @param channelVoxelTypes creates tests for each {@link VoxelDataType} in the array.
     * @throws RasterIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testThreeChannelsRGB(VoxelDataType[] channelVoxelTypes) throws RasterIOException, IOException {
        performTest(channelVoxelTypes, 3, true);
    }
    
    /**
     * Tests a stack with four-channels of unsigned 8-bit data type.
     * 
     * @throws RasterIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testFourChannels() throws RasterIOException, IOException {
        testFourChannels(DEFAULT_VOXEL_TYPE_AS_ARRAY);
    }
    
    /**
     * Tests a stack with four-channels of specified types
     * 
     * @param channelVoxelTypes creates tests for each {@link VoxelDataType} in the array.
     * @throws RasterIOException if an error occurs by the writer
     * @throws IOException if an error occurs attempting a comparison
     */
    public void testFourChannels(VoxelDataType[] channelVoxelTypes) throws RasterIOException, IOException {
        performTest(channelVoxelTypes, 4, false);
    }
    
    private void performTest(VoxelDataType[] channelVoxelTypes, int numberChannels, boolean makeRGB) throws RasterIOException, IOException {
        for(VoxelDataType voxelType : channelVoxelTypes) {
            performTest(voxelType, numberChannels, makeRGB);
        }
    }
    
    private void performTest(VoxelDataType channelVoxelType, int numberChannels, boolean makeRGB) throws RasterIOException, IOException {
        test(channelVoxelType, numberChannels, makeRGB, ChannelFixture.SMALL_2D, false);
        if (include3D) {
            test(channelVoxelType, numberChannels, makeRGB, ChannelFixture.SMALL_3D, true);
        }
    }

    private void test(VoxelDataType channelVoxelType, int numberChannels, boolean makeRGB, Extent extent, boolean do3D) throws RasterIOException, IOException {
        
        String filename = IdentifierHelper.identiferFor(numberChannels, makeRGB, do3D, EXTENT_IDENTIFIER, channelVoxelType);
                
        Stack stack = StackFixture.create(numberChannels, extent, channelVoxelType);
        Path pathWritten = writer.writeStackWithExtension(stack, directoryToWriteTo.resolve(filename), makeRGB, RasterWriteOptions.rgbMaybe3D() );
        comparer.assertComparisons(pathWritten, filename);
    }
}
