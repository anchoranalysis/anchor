/*-
 * #%L
 * anchor-io-bioformats
 * %%
 * Copyright (C) 2010 - 2025 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.io.bioformats.metadata;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.test.TestLoader;
import org.junit.jupiter.api.Test;

/**
 * Base class for testing metadata readers.
 *
 * @param <T> the type of metadata that is expected to be read
 */
abstract class MetadataReaderBaseTest<T> {

    /**
     * The name of the subdirectory in {@code src/test/resources} where the image files are located.
     */
    private static final String SUBDIRECTORY_NAME = "exif";

    private TestLoader loader = TestLoader.createFromMavenWorkingDirectory();

    @Test
    void testWithoutExif() throws ImageIOException {
        test("exif_absent.jpg", expectedExifAbsent());
    }

    @Test
    void testNoRotation() throws ImageIOException {
        test("exif_present_no_rotation_needed.jpg", expectedNoRotation());
    }

    @Test
    void testRotationNeeded() throws ImageIOException {
        test("exif_present_rotation_needed.jpg", expectedRotation());
    }

    @Test
    void testLocation() throws ImageIOException {
        test("exif_present_location.jpg", expectedLocation());
    }

    /**
     * Calculates the <i>actual</i> test item to be compared to the <i>expected</i> item given a
     * path.
     *
     * @param path the path where the file exists that may be used to calculate the actual.
     * @return the actual test item as calculated from <i>path</i> or None where appropriate.
     * @throws ImageIOException if there is a problem calculating the actual item to test.
     */
    protected abstract Optional<T> calculateActual(Path path) throws ImageIOException;

    /**
     * What is the expected-value when the test is run on the <i>exif_absent.jpg</i> file?
     *
     * <p>This file has no associated EXIF metadata.
     *
     * @return the expected-value.
     */
    protected abstract Optional<T> expectedExifAbsent();

    /**
     * What is the expected-value when the test is run on the
     * <i>exif_present_no_rotation_needed.jpg</i> file?
     *
     * @return the expected-value.
     */
    protected abstract Optional<T> expectedNoRotation();

    /**
     * What is the expected-value when the test is run on the
     * <i>exif_present_rotation_needed.jpg</i> file?
     *
     * @return the expected-value.
     */
    protected abstract Optional<T> expectedRotation();

    /**
     * What is the expected-value when the test is run on the <i>exif_present_location.jpg</i> file?
     *
     * @return the expected-value.
     */
    protected abstract Optional<T> expectedLocation();

    protected void test(String filename, Optional<T> expected) throws ImageIOException {
        Path path = loader.resolveTestPath(SUBDIRECTORY_NAME + "/" + filename);
        Optional<T> actual = calculateActual(path);
        assertEquals(expected, actual);
    }
}
