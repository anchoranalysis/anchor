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

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.core.stack.Stack; // NOSONAR
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.writer.StackWriter;
import org.anchoranalysis.image.io.stack.output.StackWriteOptions;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;

/**
 * Settings and methods for writing to the filesystem from a generator.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneratorOutputter {

    /**
     * Gets the default {@link StackWriter}.
     *
     * @param settings the settings that influence how outputs are written.
     * @return the default {@link StackWriter}.
     * @throws ImageIOException if a default writer doesn't exist.
     */
    public static StackWriter writer(OutputWriteSettings settings) throws ImageIOException {
        // We need duplicate the writer to help make it thread safe. Unsure if this is necessary or
        // not.
        Optional<StackWriter> defaultWriter = settings.getWriterInstance(StackWriter.class);
        if (defaultWriter.isPresent()) {
            return defaultWriter.get().duplicateBean();
        } else {
            throw new ImageIOException("No default stackWriter has been set");
        }
    }

    /**
     * The file extension to use for the default {@link StackWriter}, as returned by {@link
     * #writer(OutputWriteSettings)}.
     *
     * @param settings the parameters that influence how outputs are written, in general.
     * @param writeOptions parameters that influence how {@link Stack}s are written.
     * @param logger a logger, to write informative messages or non-fatal errors to.
     * @return the file extension (without a leading period) to be used by the default writer.
     * @throws ImageIOException if a default writer doesn't exist.
     */
    public static String fileExtensionWriter(
            OutputWriteSettings settings, StackWriteOptions writeOptions, Optional<Logger> logger)
            throws ImageIOException {
        return writer(settings)
                .fileFormatWarnUnexpected(writeOptions, logger)
                .getDefaultExtension();
    }
}
