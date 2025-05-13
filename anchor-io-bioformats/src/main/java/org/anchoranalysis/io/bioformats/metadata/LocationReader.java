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

import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.image.core.stack.ImageLocation;
import org.anchoranalysis.image.io.ImageIOException;

/**
 * Reads the <b>GPS location</b> from EXIF metadata, if specified in an image-file.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationReader {

    /**
     * Reads the image-location from geolocation metadata-tags, if they are present in an image.
     *
     * <p>If more than one GpsDirectory exists in the metadata, the first encountered is returned.
     * This is assumed to be an extremely rare (possibly illegal) state.
     *
     * @param path the path to the image that may or may not contain geolocation metadata.
     * @return the image-location, iff it can be determined.
     * @throws ImageIOException if the metadata cannot be read.
     */
    public static Optional<ImageLocation> readLocation(Path path) throws ImageIOException {
        return OptionalUtilities.flatMap(
                MetadataReader.readMetadata(path), LocationReader::readLocation);
    }

    /**
     * Reads the image-location from geolocation metadata-tags, if they are present in the metadata.
     *
     * <p>If more than one GpsDirectory exists in the metadata, the first encountered is returned.
     * This is assumed to be an extremely rare (possibly illegal) state.
     *
     * @param metadata the metadata to read from.
     * @return the image-location, iff it exists in the metadata.
     */
    public static Optional<ImageLocation> readLocation(Metadata metadata) {
        Collection<GpsDirectory> directories = metadata.getDirectoriesOfType(GpsDirectory.class);
        for (GpsDirectory directory : directories) {
            // Try to read out the location, making sure it's non-zero
            GeoLocation location = directory.getGeoLocation();
            if (location != null && !location.isZero()) {
                return Optional.of(
                        new ImageLocation(location.getLatitude(), location.getLongitude()));
            }
        }
        return Optional.empty();
    }
}
