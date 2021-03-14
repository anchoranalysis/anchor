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

import ij.ImagePlus;
import ij.io.FileSaver;
import java.nio.file.Path;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.writer.StackWriter;
import org.anchoranalysis.image.io.stack.output.StackWriteOptions;
import org.anchoranalysis.io.imagej.convert.ConvertToImagePlus;
import org.anchoranalysis.io.imagej.convert.ImageJConversionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for writing a raster using ImageJ.
 *
 * @author Owen Feehan
 */
public abstract class ImageJRasterWriter extends StackWriter {

    private static Log log = LogFactory.getLog(ImageJRasterWriter.class);

    @Override
    public void writeStack(Stack stack, Path filePath, StackWriteOptions options)
            throws ImageIOException {
        if (!(stack.getNumberChannels() == 1 || stack.getNumberChannels() == 3)) {
            throw new ImageIOException("Stack must have 1 or 3 channels");
        }

        if (!stack.allChannelsHaveIdenticalType()) {
            throw new ImageIOException("Stack must have identically-typed channels");
        }

        if (stack.getNumberChannels() == 3 && !stack.isRGB()) {
            throw new ImageIOException(
                    "A three-channeled stack must have the RGB flag set to true.");
        }

        writeStackTime(stack, filePath, options.getAttributes().writeAsRGB(stack));
    }

    /**
     * Writes an annotation to the filesystem at {@code outPath}.
     *
     * @param fileSaver imagej class for saving files
     * @param path where to write the annotation to
     * @param asStack whether the output will produce a stack (many images together) or not.
     * @return true if successfully written.
     * @throws ImageIOException if anything goes wrong writing the input.
     */
    protected abstract boolean writeRaster(FileSaver fileSaver, String path, boolean asStack)
            throws ImageIOException;

    /**
     * Writes a stack as a time-sequence (many images together in a single file.).
     *
     * @param stack the stack to write
     * @param path where on the filesystem to write to
     * @param makeRGB if true, the image is saved as a RGB image rather than independent channels.
     * @throws ImageIOException if anything goes wrong writing.
     */
    protected void writeStackTime(Stack stack, Path path, boolean makeRGB) throws ImageIOException {

        log.debug(String.format("Writing image %s", path));

        Dimensions dimensions = stack.getChannel(0).dimensions();

        ImagePlus image;
        try {
            image = ConvertToImagePlus.from(stack, makeRGB);
        } catch (ImageJConversionException e) {
            throw new ImageIOException(e);
        }

        try {
            writeImagePlus(image, path, stack.hasMoreThanOneSlice());
        } finally {
            image.close();
        }

        if (image.getNSlices() != dimensions.z()) {
            throw new ImageIOException(
                    String.format(
                            "The number of slices in the ImagePlus (%d) is not the same as the image dimensions (%d)",
                            image.getNSlices(), dimensions.z()));
        }

        log.debug(String.format("Finished writing image %s", path));
    }

    private void writeImagePlus(ImagePlus image, Path filePath, boolean asStack)
            throws ImageIOException {

        FileSaver fileSaver = new FileSaver(image);
        if (!writeRaster(fileSaver, filePath.toString(), asStack)) {
            throw new ImageIOException(
                    String.format("An error occured in IJ writing file '%s'", filePath));
        }
    }
}
