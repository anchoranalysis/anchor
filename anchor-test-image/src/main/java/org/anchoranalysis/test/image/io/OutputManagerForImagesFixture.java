package org.anchoranalysis.test.image.io;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.test.io.output.OutputManagerFixture;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Like {@link OutputManagerFixture} but ensures a Bioformats stack-writer exists.
 *  
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class OutputManagerForImagesFixture {

    public static OutputManager createOutputManager(Optional<Path> pathForPrefixer) {
        TestReaderWriterUtilities.ensureStackWriter();
        return OutputManagerFixture.createOutputManager(pathForPrefixer);
    }
}
