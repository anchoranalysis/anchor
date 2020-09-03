package org.anchoranalysis.io.bioformats.bean;

import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.io.bioformats.ConfigureBioformatsLogging;
import org.anchoranalysis.test.TestLoader;
import org.junit.Before;
import org.junit.Test;

/**
 * Opens diverse files (across format, bit depth, number of channels etc.) and checks that expectations are met.
 * 
 * <p>See {@link ExpectedImage} for the expectations.
 * 
 * @author Owen Feehan
 *
 */
public class BioformatsReaderTest {

    static {
        ConfigureBioformatsLogging.instance().makeSureConfigured();
    }
    
    private TestLoader loader = TestLoader.createFromMavenWorkingDirectory();
    
    private ExpectedImageTester tester;
    
    private static final String EXTENSION_JPEG = "jpg";
    private static final String EXTENSION_TIFF = "tif";
    private static final String EXTENSION_PNG = "png";
    
    @Before
    public void setUp() {
        tester = new ExpectedImageTester(loader);
    }
    
    @Test
    public void testJpeg() throws RasterIOException {
        tester.assertRGBAndUnsigned8Bit(EXTENSION_JPEG, 4351, 1048);
    }
    
    @Test
    public void testTiff() throws RasterIOException {
        tester.assertRGBAndUnsigned8BitUncompressed(EXTENSION_TIFF);
        tester.assertUnsigned16Bit(EXTENSION_TIFF, 16);
        tester.assertUnsigned8BitThreeChannels(EXTENSION_TIFF, ExpectedImageTester.COUNT_RGB_UNCOMPRESSED);
        tester.assertUnsigned16BitThreeChannels(EXTENSION_TIFF, 62);
    }
    
    @Test
    public void testPng() throws RasterIOException {
        tester.assertRGBAndUnsigned8BitUncompressed(EXTENSION_PNG);
    }
}
