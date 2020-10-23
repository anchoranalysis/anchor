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
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.generator.raster.series.StackSeries;
import org.anchoranalysis.image.io.stack.StackWriteOptions;

/**
 * A base-class for a raster-writer that writes only one or three channeled images, and a flexible
 * extension.
 *
 * @author Owen Feehan
 */
public abstract class OneOrThreeChannelsWriter extends StackWriter {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String extension = "png";
    // END BEAN PROPERTIES

    @Override
    public String fileExtension(StackWriteOptions writeOptions) {
        return extension;
    }

    @Override
    public void writeStackSeries(
            StackSeries stackSeries, Path filePath, boolean makeRGB, StackWriteOptions options)
            throws ImageIOException {
        throw new ImageIOException("Writing time-series is unsupported for this format");
    }

    @Override
    public void writeStack(
            Stack stack, Path filePath, StackWriteOptions options)
            throws ImageIOException {

        if (stack.getNumberChannels() == 1 && options.isRgb()) {
            throw new ImageIOException("1-channel images cannot be created as RGB");
        }

        if (stack.getNumberChannels() == 3 && !options.isRgb()) {
            throw new ImageIOException("3-channel images can only be created as RGB");
        }

        writeStackAfterCheck(stack, filePath);
    }

    protected abstract void writeStackAfterCheck(Stack stack, Path filePath)
            throws ImageIOException;
}
