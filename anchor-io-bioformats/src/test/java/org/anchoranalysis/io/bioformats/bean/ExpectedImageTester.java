/*-
 * #%L
 * anchor-io-bioformats
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
package org.anchoranalysis.io.bioformats.bean;

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.test.TestLoader;

/**
 * Runs the tests checking that different types of images meet expectations.
 *
 * <p>A different intensity-value is counted respectively for 8-bit (50) and 16-bit images (1000).
 *
 * @author Owen Feehan
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

    public void assertRGBAndUnsigned8BitUncompressed(String extension) throws ImageIOException {
        assertRGBAndUnsigned8Bit(
                extension, COUNT_RGB_UNCOMPRESSED, COUNT_UNSIGNED_8_BIT_UNCOMPRESSED);
    }

    public void assertRGBAndUnsigned8Bit(
            String extension, int expectedCountRGB, int expectedCountUnsigned8Bit)
            throws ImageIOException {
        assertRGB(extension, expectedCountRGB);
        assertUnsigned8Bit(extension, expectedCountUnsigned8Bit);
    }

    public void assertUnsigned16Bit(String extension, int expectedCount) throws ImageIOException {
        new ExpectedImage(
                        extension,
                        "unsigned_16bit",
                        expectedCount,
                        1,
                        UnsignedShortVoxelType.INSTANCE,
                        INTENSITY_TO_COUNT_UNSIGNED_16_BIT)
                .openAndAssert(reader, loader);
    }

    public void assertUnsigned8BitThreeChannels(String extension, int expectedCount)
            throws ImageIOException {
        new ExpectedImage(
                        extension,
                        "unsigned_8bit_three_channels",
                        expectedCount,
                        3,
                        UnsignedByteVoxelType.INSTANCE,
                        INTENSITY_TO_COUNT_UNSIGNED_8_BIT)
                .openAndAssert(reader, loader);
    }

    public void assertUnsigned16BitThreeChannels(String extension, int expectedCount)
            throws ImageIOException {
        new ExpectedImage(
                        extension,
                        "unsigned_16bit_three_channels",
                        expectedCount,
                        3,
                        UnsignedShortVoxelType.INSTANCE,
                        INTENSITY_TO_COUNT_UNSIGNED_16_BIT)
                .openAndAssert(reader, loader);
    }

    private void assertRGB(String extension, int expectedCount) throws ImageIOException {
        new ExpectedImage(
                        extension,
                        "rgb",
                        expectedCount,
                        3,
                        UnsignedByteVoxelType.INSTANCE,
                        INTENSITY_TO_COUNT_UNSIGNED_8_BIT)
                .openAndAssert(reader, loader);
    }

    private void assertUnsigned8Bit(String extension, int expectedCount) throws ImageIOException {
        new ExpectedImage(
                        extension,
                        "unsigned_8bit",
                        expectedCount,
                        1,
                        UnsignedByteVoxelType.INSTANCE,
                        INTENSITY_TO_COUNT_UNSIGNED_8_BIT)
                .openAndAssert(reader, loader);
    }
}
