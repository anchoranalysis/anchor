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
/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster;

import java.nio.file.Path;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterwriter.RasterWriter;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public abstract class RasterGenerator extends ObjectGenerator<Stack> {

    public abstract boolean isRGB() throws OutputWriteFailedException;

    @Override
    public void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException {

        Stack stack = generate();
        writeToFile(stack, outputWriteSettings, filePath, isRGB());
    }

    public static void writeToFile(
            Stack stack, OutputWriteSettings outputWriteSettings, Path filePath, boolean rgb)
            throws OutputWriteFailedException {

        try {
            RasterWriter rasterWriter =
                    RasterWriterUtilities.getDefaultRasterWriter(outputWriteSettings);
            rasterWriter.writeStack(stack, filePath, rgb);
        } catch (RasterIOException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public String getFileExtension(OutputWriteSettings outputWriteSettings) {
        return RasterWriterUtilities.getDefaultRasterFileExtension(outputWriteSettings);
    }
}
