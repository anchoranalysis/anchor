/* (C)2020 */
package org.anchoranalysis.annotation.io;

import java.io.IOException;
import java.nio.file.Path;
import org.anchoranalysis.annotation.Annotation;

public interface AnnotationWriter<T extends Annotation> {

    /**
     * Saves the annotation to the file-system
     *
     * @param annotation the annotation to save
     * @param path the path to write to (or a slightly-modified path is derived from this)
     * @throws IOException
     */
    void write(T annotation, Path path) throws IOException;
}
