package org.anchoranalysis.io.bioformats.metadata;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.time.ExecutionTimeRecorderIgnore;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.stack.input.OpenedImageFile;
import org.anchoranalysis.io.bioformats.bean.BioformatsReader;
import org.anchoranalysis.spatial.box.Extent;
import org.mockito.Mockito;

class ExtentReaderTest extends MetadataReaderBaseTest<Extent> {

    /**
     * The size inferred from the two images that are landscape (i.e. width greater than height).
     */
    private static final Optional<Extent> LANDSCAPE = Optional.of(new Extent(519, 389));

    /** The size inferred from the one images that is portrait (i.e. height greater than width). */
    private static final Optional<Extent> PORTRAIT = Optional.of(new Extent(5184, 3888));

    @Override
    protected Optional<Extent> calculateActual(Path path) throws ImageIOException {
        BioformatsReader reader = new BioformatsReader();
        OpenedImageFile file = reader.openFile(path, new ExecutionTimeRecorderIgnore());
        Logger logger = Mockito.mock(Logger.class);
        Dimensions dimensions = file.dimensionsForSeries(0, logger);
        return Optional.of(new Extent(dimensions.x(), dimensions.y()));
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
}
