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

package org.anchoranalysis.annotation.io;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.annotation.Annotation;
import org.anchoranalysis.core.time.OperationContext;
import org.anchoranalysis.io.input.InputReadFailedException;

/**
 * Allows an annotation to be <b>read</b> from the file-system.
 *
 * @param <T> annotation-type.
 * @author Owen Feehan
 */
public interface AnnotationReader<T extends Annotation> {

    /**
     * Reads an annotation, if possible, from the file-system.
     *
     * @param path a path representing the annotation (or we derive another path from this path).
     * @param context the {@link OperationContext} for the read operation.
     * @return the annotation or {@link Optional#empty()} if it can't be read.
     * @throws InputReadFailedException if the annotation cannot be read successfully.
     */
    Optional<T> read(Path path, OperationContext context) throws InputReadFailedException;
}
