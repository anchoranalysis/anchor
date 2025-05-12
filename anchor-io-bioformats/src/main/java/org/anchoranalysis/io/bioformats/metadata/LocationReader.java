package org.anchoranalysis.io.bioformats.metadata;

import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
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
        Metadata metadata = MetadataReader.readMetadata(path).get();
        return readLocation(metadata);
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
            GeoLocation geoLocation = directory.getGeoLocation();
            if (geoLocation != null && !geoLocation.isZero()) {
                return Optional.of(
                        new ImageLocation(geoLocation.getLatitude(), geoLocation.getLongitude()));
            }
        }
        return Optional.empty();
    }
}
