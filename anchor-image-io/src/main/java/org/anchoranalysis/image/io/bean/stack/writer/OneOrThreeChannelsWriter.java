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
package org.anchoranalysis.image.io.bean.stack.writer;

import java.nio.file.Path;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.format.FileFormatFactory;
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.stack.output.StackRGBState;
import org.anchoranalysis.image.io.stack.output.StackWriteOptions;

/**
 * A base-class for a {@link StackWriter} that writes an image that has either one or three channels.
 * 
 * <p>The {@link StackWriter} must also support a flexible extension, which the user can specify.
 *
 * @author Owen Feehan
 */
public abstract class OneOrThreeChannelsWriter extends StackWriter {

    // START BEAN PROPERTIES
	/** Which extension to use to write the image (without any leading period). */
    @BeanField @Getter @Setter private String extension = "png";
    // END BEAN PROPERTIES

    @Override
    public ImageFileFormat fileFormat(StackWriteOptions writeOptions) throws ImageIOException {
        return FileFormatFactory.createImageFormat(extension)
                .orElseThrow(
                        () ->
                                new ImageIOException(
                                        String.format(
                                                "The extension %s is not associated with a recognised format",
                                                extension)));
    }

    @Override
    public void writeStack(Stack stack, Path filePath, StackWriteOptions options)
            throws ImageIOException {

        if (stack.getNumberChannels() == 3
                && options.getAttributes().getRgb() != StackRGBState.RGB_WITHOUT_ALPHA) {
            throw new ImageIOException("3-channel images can only be created as RGB");
        }

        writeStackAfterCheck(stack, filePath);
    }

    /**
     * Writes the {@link Stack} to the file-system, after a check has already occurred that the correct number of channels exist.
     * 
     * @param stack the stack to write.
     * @param filePath the path to write the image to.
     * @throws ImageIOException if unable to successfully write the image.
     */
    protected abstract void writeStackAfterCheck(Stack stack, Path filePath)
            throws ImageIOException;
}
