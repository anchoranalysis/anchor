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
import org.anchoranalysis.annotation.io.AnnotationDeleter;
import org.anchoranalysis.annotation.mark.DualMarksAnnotation;

/**
 * Deletes {@link DualMarksAnnotation}s from the file-system.
 *
 * @author Owen Feehan
 */
public class DualMarksAnnotationDeleter implements AnnotationDeleter {

    @Override
    public void delete(Path annotationPath) throws IOException {
        Files.deleteIfExists(annotationPath);
        Files.deleteIfExists(TempPathCreator.deriveTempPath(annotationPath));
    }
}
