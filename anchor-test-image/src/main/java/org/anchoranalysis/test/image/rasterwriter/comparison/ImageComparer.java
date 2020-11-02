package org.anchoranalysis.test.image.rasterwriter.comparison;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Base class for a method comparing two image files.
 * 
 * @author Owen Feehan
 *
 */
public interface ImageComparer {

    /**
     * Asserts that two stacks being compared are identical.
     *
     * @param filenameWithoutExtension the filename before extensions were added.
     * @param filenameWithExtension the filename with an extension added.
     * @param path a path to be displayed if an error occurs. 
     * @throws IOException
     */
    void assertIdentical(
        String filenameWithoutExtension,
        String filenameWithExtension,
        Path path
       ) throws IOException;
}
