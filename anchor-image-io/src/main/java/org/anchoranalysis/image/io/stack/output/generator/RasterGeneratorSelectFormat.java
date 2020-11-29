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

package org.anchoranalysis.image.io.stack.output.generator;

import java.nio.file.Path;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.writer.StackWriter;
import org.anchoranalysis.image.io.stack.output.StackWriteOptions;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * A {@link RasterGenerator} that selects an appropriate output-format based upon each generated
 * image.
 *
 * @author Owen Feehan
 */
public abstract class RasterGeneratorSelectFormat<T> extends RasterGenerator<T> {

    /**
     * Combines stack-write-options derived for a particular stack with the general write-options
     * associated with the {@link RasterGenerator}.
     */
    @Override
    protected String selectFileExtension(
            Stack stack, StackWriteOptions options, OutputWriteSettings settings)
            throws OperationFailedException {
        return GeneratorOutputter.fileExtensionWriter(settings, options);
    }

    @Override
    protected void writeToFile(
            T element,
            Stack transformedElement,
            StackWriteOptions options,
            OutputWriteSettings settings,
            Path filePath)
            throws OutputWriteFailedException {
        try {
            StackWriter writer = GeneratorOutputter.writer(settings);
            writer.writeStack(transformedElement, filePath, options);
        } catch (ImageIOException e) {
            throw new OutputWriteFailedException(e);
        }
    }
}
