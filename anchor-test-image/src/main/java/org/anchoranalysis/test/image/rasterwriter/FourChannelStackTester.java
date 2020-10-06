package org.anchoranalysis.test.image.rasterwriter;

import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterwriter.RasterWriter;
import org.anchoranalysis.image.io.rasterwriter.RasterWriteOptions;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.test.image.ChannelFixture;
import org.anchoranalysis.test.image.DualComparerTemporaryFolder;
import org.anchoranalysis.test.image.StackFixture;
import lombok.AllArgsConstructor;

/**
 * Helper methods to test a {@code RasterWriter} on stacks with between 1 and 4 channels.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public class FourChannelStackTester {

    /** A minimum file-size for all written rasters, below which we assume an error has occurred. */
    private static final int MINIMUM_FILE_SIZE = 20;
    
    /** The writer to use for creating new raster-files that are tested for bytewise equality against saved rasters. */
    private RasterWriter writer;
    
    /** The comparer used for comparing the newly create files with the saved rasters. */
    private final DualComparerTemporaryFolder comparer;
    
    /** The extension to use for writing and testing files.
     * 
     *  <p>This is case-sensitive.
     *  <p>This shouldn't include the full-stop.
     */
    private final String extension;
    
    /** A prefix that is appended before every filename. */
    private final String prefix;
    
    /** If true, then 3D stacks are also tested and saved, not just 2D stacks. */
    private final boolean include3D;
    
    public void testSingleChannel() throws RasterIOException, IOException {
        performTest("singleChannel", 1, false);
    }
    
    /**
     * Tests the creation of a single-channel stack with the RGB flag on, which should typically produce an exception.
     * 
     * @throws RasterIOException
     * @throws IOException
     */
    public void testSingleChannelRGB() throws RasterIOException, IOException {
        performTest("singleChannel", 1, true);
    }
        
    public void testTwoChannels() throws RasterIOException, IOException {
        performTest("twoChannels", 2, false);
    }
    
    /**
     * Tests the creation of a three-channel stack with the rgb-flag set to false.
     * 
     * @throws RasterIOException
     * @throws IOException
     */
    public void testThreeChannelsSeparate() throws RasterIOException, IOException {
        performTest("threeChannelsSeparate", 3, false);
    }

    /**
     * Tests the creation of a three-channel stack with the rgb-flag set to true.
     * 
     * @throws RasterIOException
     * @throws IOException
     */
    public void testThreeChannelsRGB() throws RasterIOException, IOException {
        performTest("threeChannelsRGB", 3, true);
    }
    
    public void testFourChannels() throws RasterIOException, IOException {
        performTest("fourChannels", 4, false);
    }
    
    private void performTest(String appendToPrefix, int numberChannels, boolean makeRGB) throws RasterIOException, IOException {
        test(prefix + appendToPrefix + "_2D_small", numberChannels, makeRGB, ChannelFixture.SMALL_2D);
        if (include3D) {
            test(prefix + appendToPrefix + "_3D_small", numberChannels, makeRGB, ChannelFixture.SMALL_3D);
        }
    }
    
    private void test(String filename, int numberChannels, boolean makeRGB, Extent extent) throws RasterIOException, IOException {
        Stack stack = StackFixture.create(numberChannels, extent);
        
        Path pathWritten = writer.writeStackWithExtension(stack, comparer.resolveTemporaryFile(filename), makeRGB, RasterWriteOptions.rgbMaybe3D() );
        
        assertTrue( filename + "_minimumFileSize", Files.size(pathWritten) > MINIMUM_FILE_SIZE );
        
        try {
            assertTrue( filename + "_binaryCompare", comparer.compareTwoBinaryFiles(filename + "." + extension) );
        } catch (IOException e) {
            System.err.printf("The test wrote a file to temporary-folder directory at:%n%s%n", pathWritten);
            throw new IOException(
              String.format("The comparer threw an IOException, which likely means it cannot find an appropriate raster to compare against for %s.", filename), e );
        }
    }
}
