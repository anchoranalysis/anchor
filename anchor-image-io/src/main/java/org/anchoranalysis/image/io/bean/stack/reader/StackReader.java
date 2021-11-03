/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.bean.stack.reader;

import java.nio.file.Path;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.progress.ProgressIgnore;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.stack.input.OpenedImageFile;

/**
 * Reads an image-{@link Stack} from the file-system.
 *
 * @author Owen Feehan
 */
public abstract class StackReader extends AnchorBean<StackReader> {

    /**
     * Opens a file at {@code path} that should contain only a single-stack.
     *
     * @param path the path of the image-file to open.
     * @return the stack that has been read.
     * @throws ImageIOException if there is a series of stacks in the file, or if anything else goes
     *     wrong.
     */
    public Stack readStack(Path path) throws ImageIOException {
        OpenedImageFile openedFile = openFile(path);

        try {
            if (openedFile.numberSeries() != 1) {
                throw new ImageIOException("there must be exactly one series");
            }

            return openedFile.open(0, ProgressIgnore.get()).get(0);
        } finally {
            openedFile.close();
        }
    }

    /**
     * Opens a file containing one or more images but does not read an image.
     *
     * @param path where the file is located.
     * @return an interface to the opened file that should be closed when no longer in use.
     * @throws ImageIOException if the file cannot be read.
     */
    public abstract OpenedImageFile openFile(Path path) throws ImageIOException;
}
