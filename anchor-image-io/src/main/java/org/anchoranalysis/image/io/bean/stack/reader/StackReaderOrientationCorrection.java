/*-
 * #%L
 * anchor-image-io
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.io.bean.stack.reader;

import java.nio.file.Path;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.stack.CalculateOrientationChange;
import org.anchoranalysis.image.io.stack.input.OpenedImageFile;

/**
 * A {@link StackReader} that supports the correction of orientation as images are read from the
 * file-system.
 *
 * @author Owen Feehan
 */
public abstract class StackReaderOrientationCorrection extends StackReader {

    /**
     * Opens a file containing one or more images but does not read an image.
     *
     * @param path where the file is located.
     * @param orientationCorrection calculates any needed correction applied to the orientation as
     *     the image is loaded.
     * @param executionTimeRecorder records the execution-times of certain operations.
     * @return an interface to the opened file that should be closed when no longer in use.
     * @throws ImageIOException if the file cannot be read.
     */
    public abstract OpenedImageFile openFile(
            Path path,
            CalculateOrientationChange orientationCorrection,
            ExecutionTimeRecorder executionTimeRecorder)
            throws ImageIOException;
}
