/*-
 * #%L
 * anchor-io-ij
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

package org.anchoranalysis.io.imagej.bean.stack.writer;

import ij.io.FileSaver;
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.stack.output.StackWriteOptions;

/**
 * Writes a PNG image using ImageJ.
 *
 * <p>The extension .png is employed by default.
 *
 * @author Owen Feehan
 */
public class PNG extends NoTimeSeries {

    @Override
    protected boolean writeRaster(FileSaver fileSaver, String path, boolean asStack)
            throws ImageIOException {

        if (asStack) {
            throw new ImageIOException("Writing as stack unsupported for this format");
        } else {
            return fileSaver.saveAsPng(path);
        }
    }

    @Override
    public ImageFileFormat fileFormat(StackWriteOptions writeOptions) {
        return ImageFileFormat.PNG;
    }
}
