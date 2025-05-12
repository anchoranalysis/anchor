/*-
 * #%L
 * anchor-io-bioformats
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.anchoranalysis.image.io.ImageIOException;

/**
 * Tests {@link AcquisitionDateReader}.
 *
 * @author Owen Feehan
 */
class AcqusitionDateReaderTest extends MetadataReaderBaseTest<Instant> {

    // As the image doesn't have a time zone offset set, it will use the system-default.
    private static final ZonedDateTime EXPECTED_NO_ROTATION =
            ZonedDateTime.of(2021, 07, 10, 10, 20, 34, 0, ZoneId.systemDefault());

    private static final ZonedDateTime EXPECTED_ROTATION =
            ZonedDateTime.of(2016, 10, 28, 10, 43, 48, 0, ZoneId.systemDefault());

    @Override
    protected Optional<Instant> calculateActual(Path path) throws ImageIOException {
        // We compare instants in case as the time-zone for a given offset is system-dependent.
        return AcquisitionDateReader.readAcquisitionDate(path).map(ZonedDateTime::toInstant);
    }

    @Override
    protected Optional<Instant> expectedExifAbsent() {
        return Optional.empty();
    }

    @Override
    protected Optional<Instant> expectedNoRotation() {
        return Optional.of(EXPECTED_NO_ROTATION.toInstant());
    }

    @Override
    protected Optional<Instant> expectedRotation() {
        return Optional.of(EXPECTED_ROTATION.toInstant());
    }

    @Override
    protected Optional<Instant> expectedLocation() {
        return Optional.empty();
    }
}
