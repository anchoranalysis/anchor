package org.anchoranalysis.io.bioformats.metadata;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.test.TestLoader;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link AcquisitionDateReader}.
 *
 * @author Owen Feehan
 */
class AcqusitionDateReaderTest {

    /**
     * The name of the subdirectory in {@code src/test/resources} where the image files are located.
     */
    private static final String SUBDIRECTORY_NAME = "exif";

    private TestLoader loader = TestLoader.createFromMavenWorkingDirectory();

    @Test
    void testWithoutExif() throws ImageIOException {
        test("exif_absent.jpg", Optional.empty());
    }

    @Test
    void testWithExif() throws ImageIOException {
        test("exif_present_no_rotation_needed.jpg", Optional.empty());
    }

    private void test(String filename, Optional<Date> expectedOrientation) throws ImageIOException {
        Path path = loader.resolveTestPath(SUBDIRECTORY_NAME + "/" + filename);
        assertEquals(expectedOrientation, AcquisitionDateReader.readAcquisitionDate(path));
    }
}
