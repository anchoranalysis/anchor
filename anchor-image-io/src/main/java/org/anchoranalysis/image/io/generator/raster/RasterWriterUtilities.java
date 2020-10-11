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

import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.stack.StackWriter;
import org.anchoranalysis.image.io.stack.StackWriteOptions;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RasterWriterUtilities {

    public static void writeRasterUsingDefault(
            Stack stack,
            OutputWriteSettings outputWriteSettings,
            Path filePath,
            boolean rgb,
            StackWriteOptions writeOptions)
            throws OutputWriteFailedException {

        try {
            StackWriter writer =
                    RasterWriterUtilities.getDefaultRasterWriter(outputWriteSettings);
            writer.writeStack(stack, filePath, rgb, writeOptions);
        } catch (RasterIOException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    /**
     * Gets the default raster-writer associated with outputWriteSettings
     *
     * @param outputWriteSettings
     * @return a writer (always non-null)
     * @throws RasterIOException if a writer doesn't exist
     */
    public static StackWriter getDefaultRasterWriter(OutputWriteSettings outputWriteSettings)
            throws RasterIOException {
        StackWriter defaultWriter =
                (StackWriter) outputWriteSettings.getWriterInstance(StackWriter.class);
        if (defaultWriter == null) {
            throw new RasterIOException("No default stackWriter has been set");
        }
        return defaultWriter;
    }

    public static String fileExtensionForDefaultRasterWriter(
            OutputWriteSettings outputWriteSettings, StackWriteOptions rasterOptions)
            throws OperationFailedException {
        try {
            return getDefaultRasterWriter(outputWriteSettings).fileExtension(rasterOptions);
        } catch (RasterIOException e) {
            throw new OperationFailedException(e);
        }
    }
}
