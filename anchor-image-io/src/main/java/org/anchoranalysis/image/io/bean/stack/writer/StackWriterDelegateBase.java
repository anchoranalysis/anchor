/*-
 * #%L
 * anchor-plugin-io
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
package org.anchoranalysis.image.io.bean.stack.writer;

import java.nio.file.Path;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.stack.StackSeries;
import org.anchoranalysis.image.io.stack.output.StackWriteOptions;

/**
 * A base class for a {@link StackWriter} delegates to another {@link StackWriter} based on values
 * of a {@link StackWriteOptions}.
 *
 * @author Owen Feehan
 */
public abstract class StackWriterDelegateBase extends StackWriter {

    @Override
    public String fileExtension(StackWriteOptions writeOptions) {
        return selectDelegate(writeOptions).fileExtension(writeOptions);
    }

    @Override
    public void writeStack(Stack stack, Path filePath, StackWriteOptions options)
            throws ImageIOException {
        selectDelegate(options).writeStack(stack, filePath, options);
    }

    @Override
    public void writeStackSeries(StackSeries stackSeries, Path filePath, StackWriteOptions options)
            throws ImageIOException {
        selectDelegate(options).writeStackSeries(stackSeries, filePath, options);
    }

    /**
     * Selects a {@link StackWriter} to use as a delegate.
     *
     * @param writeOptions options that specify what kind of rasters will be written.
     * @return the selected {@link StackWriter}
     */
    protected abstract StackWriter selectDelegate(StackWriteOptions writeOptions);
}
