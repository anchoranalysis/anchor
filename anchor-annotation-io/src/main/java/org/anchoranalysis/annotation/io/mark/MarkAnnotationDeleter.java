/* (C)2020 */
package org.anchoranalysis.annotation.io.mark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.anchoranalysis.annotation.io.AnnotationDeleter;

public class MarkAnnotationDeleter implements AnnotationDeleter {

    @Override
    public void delete(Path annotationPath) throws IOException {
        Files.deleteIfExists(annotationPath);
        Files.deleteIfExists(TempPathCreator.deriveTempPath(annotationPath));
    }
}
