package org.anchoranalysis.io.bioformats.metadata;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    // As the image doesn't have a time zone offset set, it will use the system-default.
    private static final ZonedDateTime EXPECTED_ACQUISITION_TIME =
            ZonedDateTime.of(2021, 07, 10, 10, 20, 34, 0, ZoneId.systemDefault());

    @Test
    void testWithoutExif() throws ImageIOException {
        test("exif_absent.jpg", Optional.empty());
    }

    @Test
    void testWithExif() throws ImageIOException, ParseException {
        // We compare instants in case as the time-zone for a given offset is system-dependent.
        test(
                "exif_present_no_rotation_needed.jpg",
                Optional.of(EXPECTED_ACQUISITION_TIME.toInstant()));
    }

    private void test(String filename, Optional<Instant> expectedDate) throws ImageIOException {
        Path path = loader.resolveTestPath(SUBDIRECTORY_NAME + "/" + filename);
        Optional<Instant> instant =
                AcquisitionDateReader.readAcquisitionDate(path).map(ZonedDateTime::toInstant);
        assertEquals(expectedDate, instant);
    }
}
