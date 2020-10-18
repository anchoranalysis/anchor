/*-
 * #%L
 * anchor-annotation-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.annotation.io.mark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.annotation.Annotation;
import org.anchoranalysis.annotation.io.AnnotationWriter;
import org.anchoranalysis.annotation.io.WriterUtilities;
import org.anchoranalysis.annotation.mark.DualMarksAnnotation;
import org.anchoranalysis.core.serialize.XStreamSerializer;

/**
 * @author Owen Feehan
 * @param <T> rejection-reason
 */
public class MarkAnnotationWriter<T> implements AnnotationWriter<DualMarksAnnotation<T>> {

    @Getter @Setter private boolean disablePathModification = false;

    @Override
    public void write(DualMarksAnnotation<T> annotation, Path path) throws IOException {

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

        XStreamSerializer.serializeObjectToFile(annotation, annotationPath);

        if (annotationPathForDeletion != null
                && !annotationPathForDeletion.equals(annotationPath)) {
            Files.deleteIfExists(annotationPathForDeletion);
        }
    }
}
