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

import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.io.bioformats.ConfigureBioformatsLogging;
import org.anchoranalysis.test.TestLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Opens diverse files (across format, bit depth, number of channels etc.) and checks that
 * expectations are met.
 *
 * <p>See {@link ExpectedImage} for the expectations.
 *
 * @author Owen Feehan
 */
class BioformatsReaderTest {

    static {
        ConfigureBioformatsLogging.instance().makeSureConfigured();
    }

    private TestLoader loader = TestLoader.createFromMavenWorkingDirectory();

    private ExpectedImageTester tester;

    private static final String EXTENSION_JPEG = ImageFileFormat.JPEG.getDefaultExtension();
    private static final String EXTENSION_TIFF = ImageFileFormat.TIFF.getDefaultExtension();
    private static final String EXTENSION_PNG = ImageFileFormat.PNG.getDefaultExtension();

    @BeforeEach
    void setUp() {
        tester = new ExpectedImageTester(loader);
    }

    @Test
    void testJpeg() throws ImageIOException {
        tester.assertRGBAndUnsigned8Bit(EXTENSION_JPEG, 4351, 1048);
    }

    @Test
    void testTiff() throws ImageIOException, CreateException {
        tester.assertRGBAndUnsigned8BitUncompressed(EXTENSION_TIFF);
        tester.assertUnsigned16Bit(EXTENSION_TIFF, 16);
        tester.assertUnsigned8BitThreeChannels(
                EXTENSION_TIFF, ExpectedImageTester.COUNT_RGB_UNCOMPRESSED);
        tester.assertUnsigned16BitThreeChannels(EXTENSION_TIFF, 62);
    }

    @Test
    void testPng() throws ImageIOException {
        tester.assertRGBAndUnsigned8BitUncompressed(EXTENSION_PNG);
    }

    @Test
    void testWithResolution() throws ImageIOException {
        tester.assertWithResolution(EXTENSION_TIFF, 6900);
    }
}
