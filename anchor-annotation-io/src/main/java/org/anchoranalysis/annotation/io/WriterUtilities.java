/* (C)2020 */
package org.anchoranalysis.annotation.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class WriterUtilities {

    private WriterUtilities() {}

    public static void createNecessaryDirectories(Path annotationPath) throws IOException {
        // Create whatever directories we need
        Files.createDirectories(annotationPath.getParent());
    }
}
