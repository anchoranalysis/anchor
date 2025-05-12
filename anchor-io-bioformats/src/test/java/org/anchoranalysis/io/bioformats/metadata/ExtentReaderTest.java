package org.anchoranalysis.io.bioformats.metadata;

import com.drew.metadata.Metadata;
import com.drew.metadata.jpeg.JpegDirectory;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.spatial.box.Extent;

class ExtentReaderTest extends MetadataReaderBaseTest<Extent> {

    /**
     * The size inferred from the two images that are landscape (i.e. width greater than height).
     */
    private static final Optional<Extent> LANDSCAPE = Optional.of(new Extent(519, 389));

    /** The size inferred from the one images that is portrait (i.e. height greater than width). */
    private static final Optional<Extent> PORTRAIT = Optional.of(new Extent(5184, 3888));

    /** The size inferred from the one images that has location info. */
    private static final Optional<Extent> LOCATION = Optional.of(new Extent(66, 123));

    @Override
    protected Optional<Extent> calculateActual(Path path) throws ImageIOException {
        // In this case we ignore the EXIF entry (which may or may not be present) and use the JPEG
        // metadata to read the width and height.
        Metadata metadata = MetadataReader.readMetadata(path).get();
        return ExtentReader.read(
                metadata,
                JpegDirectory.class,
                JpegDirectory.TAG_IMAGE_WIDTH,
                JpegDirectory.TAG_IMAGE_HEIGHT);
    }

    @Override
    protected Optional<Extent> expectedExifAbsent() {
        return LANDSCAPE;
    }

    @Override
    protected Optional<Extent> expectedNoRotation() {
        return LANDSCAPE;
    }

    @Override
    protected Optional<Extent> expectedRotation() {
        return PORTRAIT;
    }

    @Override
    protected Optional<Extent> expectedLocation() {
        return LOCATION;
    }
}
