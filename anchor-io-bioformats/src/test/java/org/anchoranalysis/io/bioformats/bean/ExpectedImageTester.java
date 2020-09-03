package org.anchoranalysis.io.bioformats.bean;

import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.test.TestLoader;
import lombok.RequiredArgsConstructor;

/**
 * Runs the tests checking that different types of images meet expectations.
 * 
 * <p>A different intensity-value is counted respectively for 8-bit (50) and 16-bit images (1000).
 * 
 * @author Owen Feehan
 *
 */
@RequiredArgsConstructor
class ExpectedImageTester {
    
    /** The expected count for the unsigned 8 bit image in any uncompressed format. */
    private static final int COUNT_UNSIGNED_8_BIT_UNCOMPRESSED = 1054;
        
    /** The expected count for the first channel in the RGB image in any uncompressed format. */
    public static final int COUNT_RGB_UNCOMPRESSED = 4336;
    
    /** Which intensity-value to count in unsigned 8 bit images? */
    private static final int INTENSITY_TO_COUNT_UNSIGNED_8_BIT = 50;
    
    /** Which intensity-value to count in unsigned 16 bit images? */
    private static final int INTENSITY_TO_COUNT_UNSIGNED_16_BIT = 1000;
    
    // START REQUIRED ARGUMENTS
    private final TestLoader loader;
    // END REQUIRED ARGUMENTS
    
    private BioformatsReader reader = new BioformatsReader();
        
    public void assertRGBAndUnsigned8BitUncompressed(String extension) throws RasterIOException {
        assertRGBAndUnsigned8Bit(extension, COUNT_RGB_UNCOMPRESSED, COUNT_UNSIGNED_8_BIT_UNCOMPRESSED);
    }
    
    public void assertRGBAndUnsigned8Bit(String extension, int expectedCountRGB, int expectedCountUnsigned8Bit) throws RasterIOException {
        assertRGB(extension, expectedCountRGB);
        assertUnsigned8Bit(extension, expectedCountUnsigned8Bit);
    }
    
    public void assertUnsigned16Bit(String extension, int expectedCount) throws RasterIOException {
        new ExpectedImage(extension, "unsigned_16bit", expectedCount, 1, UnsignedShortVoxelType.INSTANCE, INTENSITY_TO_COUNT_UNSIGNED_16_BIT).openAndAssert(reader, loader);
    }
    
    public void assertUnsigned8BitThreeChannels(String extension, int expectedCount) throws RasterIOException {
        new ExpectedImage(extension, "unsigned_8bit_three_channels", expectedCount, 3, UnsignedByteVoxelType.INSTANCE, INTENSITY_TO_COUNT_UNSIGNED_8_BIT).openAndAssert(reader, loader);
    }
        
    private void assertRGB(String extension, int expectedCount) throws RasterIOException {
        new ExpectedImage(extension, "rgb", expectedCount, 3, UnsignedByteVoxelType.INSTANCE, INTENSITY_TO_COUNT_UNSIGNED_8_BIT).openAndAssert(reader, loader);
    }
        
    private void assertUnsigned8Bit(String extension, int expectedCount) throws RasterIOException {
        new ExpectedImage(extension, "unsigned_8bit", expectedCount, 1, UnsignedByteVoxelType.INSTANCE, INTENSITY_TO_COUNT_UNSIGNED_8_BIT).openAndAssert(reader, loader);
    }
}
