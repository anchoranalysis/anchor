/* (C)2020 */
package org.anchoranalysis.annotation.io;

import java.io.IOException;
import java.nio.file.Path;

public interface AnnotationDeleter {

    /** Deletes all files at this path (or any other paths derived from this one */
    void delete(Path path) throws IOException;
}
