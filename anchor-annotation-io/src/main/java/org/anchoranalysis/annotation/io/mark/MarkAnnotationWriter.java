/* (C)2020 */
package org.anchoranalysis.annotation.io.mark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.anchoranalysis.annotation.Annotation;
import org.anchoranalysis.annotation.io.AnnotationWriter;
import org.anchoranalysis.annotation.io.WriterUtilities;
import org.anchoranalysis.annotation.mark.MarkAnnotation;
import org.anchoranalysis.io.generator.serialized.XStreamGenerator;

public class MarkAnnotationWriter implements AnnotationWriter<MarkAnnotation> {

    private boolean disablePathModification = false;

    @Override
    public void write(MarkAnnotation annotation, Path path) throws IOException {

        Path annotationPathUnfinished = TempPathCreator.deriveTempPath(path);

        Path annotationPathChosen;
        Path annotationPathForDeletion;

        if (annotation.isFinished() || disablePathModification) {
            annotationPathChosen = path;
            annotationPathForDeletion = annotationPathUnfinished;
        } else {
            annotationPathChosen = annotationPathUnfinished;
            annotationPathForDeletion = path;
        }

        saveAnnotationNoPathChange(annotation, annotationPathChosen, annotationPathForDeletion);
    }

    /**
     * Saves an annotation to the filesystem to a specific path (without changing it)
     *
     * @param annotation annotation to be saved
     * @param annotationPath the path to save the annotation to
     * @param annotationPathForDeletion if non-null, a path (if different to annotationPath) which
     *     is deleted after a successful save
     */
    private static void saveAnnotationNoPathChange(
            Annotation annotation, Path annotationPath, Path annotationPathForDeletion)
            throws IOException {

        // Create whatever directories we need
        WriterUtilities.createNecessaryDirectories(annotationPath);

        XStreamGenerator.writeObjectToFile(annotation, annotationPath);

        if (annotationPathForDeletion != null
                && !annotationPathForDeletion.equals(annotationPath)) {
            Files.deleteIfExists(annotationPathForDeletion);
        }
    }

    public boolean isDisablePathModification() {
        return disablePathModification;
    }

    public void setDisablePathModification(boolean disablePathModification) {
        this.disablePathModification = disablePathModification;
    }
}
