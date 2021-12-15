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

import java.nio.file.Path;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.annotation.io.AnnotationReader;
import org.anchoranalysis.annotation.mark.DualMarksAnnotation;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.serialize.DeserializationFailedException;
import org.anchoranalysis.core.serialize.XStreamDeserializer;
import org.anchoranalysis.core.time.OperationContext;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.mpp.io.marks.MarkCollectionDeserializer;
import org.anchoranalysis.mpp.mark.MarkCollection;

/**
 * Reads {@link DualMarksAnnotation}s from the file-system.
 *
 * @author Owen Feehan
 * @param <T> rejection-reason type
 */
@AllArgsConstructor
public class DualMarksAnnotationReader<T> implements AnnotationReader<DualMarksAnnotation<T>> {

    private static final MarkCollectionDeserializer DESERIALIZER = new MarkCollectionDeserializer();

    private final boolean acceptUnfinished;

    @Override
    public Optional<DualMarksAnnotation<T>> read(Path path, OperationContext context)
            throws InputReadFailedException {

        Optional<Path> pathMaybeChanged = fileNameToRead(path);
        try {
            return OptionalUtilities.map(
                    pathMaybeChanged, pathToRead -> readAnnotationFromPath(pathToRead, context));
        } catch (DeserializationFailedException e) {
            throw new InputReadFailedException("Cannot deserialize annotation", e);
        }
    }

    /**
     * Whether an annotation exists at a particular path on the file-system.
     *
     * @param path the path.
     * @return true iff the annotation exists at this path.
     */
    public boolean annotationExistsAt(Path path) {
        return fileNameToRead(path).isPresent();
    }

    /**
     * Reads the annotations as a {@link MarkCollection} from the file-system.
     *
     * @param path the path where the annotations are stored.
     * @param context context for reading a stack from the file-system.
     * @return a newly created {@link MarkCollection} deserialized from {@code path}.
     * @throws DeserializationFailedException if the deserialization failed.
     */
    public MarkCollection readDefaultMarks(Path path, OperationContext context)
            throws DeserializationFailedException {
        return DESERIALIZER.deserialize(path, context);
    }

    private Optional<Path> fileNameToRead(Path annotationPath) {

        if (annotationPath.toFile().exists()) {
            return Optional.of(annotationPath);
        }

        if (!acceptUnfinished) {
            return Optional.empty();
        }

        Path pathUnfinished = TempPathCreator.deriveTempPath(annotationPath);

        if (pathUnfinished.toFile().exists()) {
            return Optional.of(pathUnfinished);
        }

        // No path to read
        return Optional.empty();
    }

    private DualMarksAnnotation<T> readAnnotationFromPath(
            Path annotationPath, OperationContext context) throws DeserializationFailedException {
        XStreamDeserializer<DualMarksAnnotation<T>> deserialized = new XStreamDeserializer<>();
        return deserialized.deserialize(annotationPath, context);
    }
}
