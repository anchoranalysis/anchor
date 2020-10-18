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

package org.anchoranalysis.image.io.bean.stack;

import java.nio.file.Path;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.stack.OpenedRaster;

public abstract class StackReader extends AnchorBean<StackReader> {

    /**
     * Opens a file at {@code path} that should contain only a single-stack.
     *
     * @param path the path of the reaster-file to open
     * @return the stack that has been read
     * @throws ImageIOException if there is a series of stacks in the file, or if anything else goes
     *     wrong
     */
    public Stack readStack(Path path) throws ImageIOException {
        OpenedRaster openedRaster = openFile(path);

        try {
            if (openedRaster.numberSeries() != 1) {
                throw new ImageIOException("there must be exactly one series");
            }

            return openedRaster.open(0, ProgressReporterNull.get()).get(0);
        } finally {
            openedRaster.close();
        }
    }

    public abstract OpenedRaster openFile(Path path) throws ImageIOException;
}
