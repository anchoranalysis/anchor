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

package org.anchoranalysis.image.io.generator.raster;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.StackWriter;
import org.anchoranalysis.image.io.stack.StackWriteOptions;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;

/**
 * Settings and methods for writing to the filesystem from a generator.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneratorOutputter {

    /**
     * Gets the default {@link StackWriter}.
     *
     * @param outputWriteSettings
     * @return a writer (always non-null)
     * @throws ImageIOException if a writer doesn't exist
     */
    public static StackWriter writer(OutputWriteSettings outputWriteSettings)
            throws ImageIOException {
        StackWriter defaultWriter =
                (StackWriter) outputWriteSettings.getWriterInstance(StackWriter.class);
        if (defaultWriter == null) {
            throw new ImageIOException("No default stackWriter has been set");
        }
        // We duplicate the writer to make it thread safe
        return defaultWriter;
    }

    public static String fileExtensionWriter(
            OutputWriteSettings outputWriteSettings, StackWriteOptions writeOptions)
            throws OperationFailedException {
        try {
            return writer(outputWriteSettings).fileExtension(writeOptions);
        } catch (ImageIOException e) {
            throw new OperationFailedException(e);
        }
    }
}
