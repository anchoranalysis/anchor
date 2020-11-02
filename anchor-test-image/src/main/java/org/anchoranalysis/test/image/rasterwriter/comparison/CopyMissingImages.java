package org.anchoranalysis.test.image.rasterwriter.comparison;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.AllArgsConstructor;

/**
 * When an error is thrown by a comparison, the generated raster at {@code path} is coped to a destination fodler.
 *
 * <p>This is useful for building a set of rasters to compare against, when they don't already
 * exist.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
class CopyMissingImages implements ImageComparer {

    /** The comparer that does the comparison. */
    private ImageComparer comparer;
    
    /** A path to a directory to copy the missing images to. */
    private Path directoryToCopyTo;
    
    @Override
    public void assertIdentical(String filenameWithoutExtension, String filenameWithExtension,
            Path path) throws IOException {
        try {
            comparer.assertIdentical(filenameWithoutExtension, filenameWithExtension, path);
        } catch (IOException e) {
            if (directoryToCopyTo.toFile().isDirectory()) {
                Files.copy(path, directoryToCopyTo.resolve(filenameWithExtension));
            } else {
                throw new IOException("directoryToCopyTo does not specify a path to a directory: " + directoryToCopyTo);
            }
        }
    }
}
