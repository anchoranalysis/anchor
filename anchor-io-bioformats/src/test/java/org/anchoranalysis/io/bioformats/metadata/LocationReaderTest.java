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
