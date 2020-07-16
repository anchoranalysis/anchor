/* (C)2020 */
package org.anchoranalysis.annotation.io;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.annotation.Annotation;
import org.anchoranalysis.io.error.AnchorIOException;

public interface AnnotationReader<T extends Annotation> {

    /**
     * Reads an annotation if it can, returns NULL otherwise
     *
     * @param path a path representing the annotation (or we derive another path from this path)
     * @return the annotation or NULL if it doesn't exist in a suitable state
     * @throws AnchorIOException
     */
    Optional<T> read(Path path) throws AnchorIOException;
}
