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

package org.anchoranalysis.io.bioformats.bean.writer;

import java.nio.file.Path;
import loci.formats.FormatException;
import loci.formats.IFormatWriter;
import loci.formats.out.TiffWriter;
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.stack.output.StackWriteOptions;

/**
 * Writes a stack to the filesystem as a TIFF using the <a
 * href="https://www.openmicroscopy.org/bio-formats/">Bioformats</a> library.
 *
 * @author Owen Feehan
 */
public class Tiff extends BioformatsWriter {

    /** Default constructor. */
    public Tiff() {
        super(true);
    }

    @Override
    public void writeStack(Stack stack, Path filePath, StackWriteOptions options)
            throws ImageIOException {

        if (!(stack.getNumberChannels() == 1 || stack.getNumberChannels() == 3)) {
            throw new ImageIOException("Stack must have 1 or 3 channels");
        }

        super.writeStack(stack, filePath, options);
    }

    // A default extension
    @Override
    public ImageFileFormat fileFormat(StackWriteOptions writeOptions) {
        return ImageFileFormat.TIFF;
    }

    @Override
    protected IFormatWriter createWriter() throws ImageIOException {
        try {
            TiffWriter writer = new TiffWriter(); // NOSONAR
            // COMPRESSION CURRENTLY DISABLED
            writer.setCompression("LZW");
            writer.setBigTiff(false);
            writer.setValidBitsPerPixel(8);
            writer.setWriteSequentially(true);
            return writer;
        } catch (FormatException e) {
            throw new ImageIOException(e);
        }
    }
}
