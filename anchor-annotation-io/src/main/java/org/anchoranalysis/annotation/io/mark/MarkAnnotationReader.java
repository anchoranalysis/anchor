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
import org.anchoranalysis.anchor.mpp.mark.MarkCollection;
import org.anchoranalysis.annotation.io.AnnotationReader;
import org.anchoranalysis.annotation.mark.MarkAnnotation;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.io.bean.deserializer.XStreamDeserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.mpp.io.marks.MarkCollectionDeserializer;

public class MarkAnnotationReader implements AnnotationReader<MarkAnnotation> {

    private boolean acceptUnfinished;

    private static final MarkCollectionDeserializer DESERIALIZER = new MarkCollectionDeserializer();

    public MarkAnnotationReader(boolean acceptUnfinished) {
        super();
        this.acceptUnfinished = acceptUnfinished;
    }

    public boolean annotationExistsCorrespondTo(Path annotationPath) {
        return fileNameToRead(annotationPath).isPresent();
    }

    @Override
    public Optional<MarkAnnotation> read(Path path) throws AnchorIOException {

        Optional<Path> pathMaybeChanged = fileNameToRead(path);
        try {
            return OptionalUtilities.map(
                    pathMaybeChanged, MarkAnnotationReader::readAnnotationFromPath);
        } catch (DeserializationFailedException e) {
            throw new AnchorIOException("Cannot deserialize annotation", e);
        }
    }

    // Reads an annotation if it can, returns NULL otherwise
    public MarkCollection readDefaultMarks(Path path) throws DeserializationFailedException {
        return DESERIALIZER.deserialize(path);
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

    private static MarkAnnotation readAnnotationFromPath(Path annotationPath)
            throws DeserializationFailedException {
        XStreamDeserializer<MarkAnnotation> deserialized = new XStreamDeserializer<>();
        return deserialized.deserialize(annotationPath);
    }
}
