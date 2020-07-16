/* (C)2020 */
package org.anchoranalysis.annotation.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Deletes any annotation that is only the file represented at the path */
public class SimpleAnnotationDeleter implements AnnotationDeleter {

    @Override
    public void delete(Path path) throws IOException {
        Files.deleteIfExists(path);
    }
}
