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

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.image.core.stack.ImageLocation;
import org.anchoranalysis.image.io.ImageIOException;

class LocationReaderTest extends MetadataReaderBaseTest<ImageLocation> {

    // See PrecisionConstants.EPSILON for the precision used when comparing doubles
    private static final ImageLocation EXPECTED_LOCATION =
            new ImageLocation(47.5561179722, 7.595919);

    @Override
    protected Optional<ImageLocation> calculateActual(Path path) throws ImageIOException {
        return LocationReader.readLocation(path);
    }

    @Override
    protected Optional<ImageLocation> expectedExifAbsent() {
        return Optional.empty();
    }

    @Override
    protected Optional<ImageLocation> expectedNoRotation() {
        return Optional.empty();
    }

    @Override
    protected Optional<ImageLocation> expectedRotation() {
        return Optional.empty();
    }

    @Override
    protected Optional<ImageLocation> expectedLocation() {
        return Optional.of(EXPECTED_LOCATION);
    }
}
