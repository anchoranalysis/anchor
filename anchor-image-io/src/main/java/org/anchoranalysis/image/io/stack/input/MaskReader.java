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

package org.anchoranalysis.image.io.stack.input;

import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.time.OperationContext;
import org.anchoranalysis.image.core.mask.Mask;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.reader.StackReader;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesInt;

/**
 * Utility functions for reading a {@link Mask} from the file-system.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaskReader {

    /**
     * Utility functions for opening a single-channeled stack as a {@link Mask}.
     *
     * @param stackReader the raster-reader for reading the stack.
     * @param path the path the raster is located at.
     * @param binaryValues what constitutes <i>on</i> and <i>off</i> voxels in the raster.
     * @param context context for reading a stack from the file-system.
     * @return a newly created {@link Mask} as read from the file-system.
     * @throws ImageIOException if the underlying image cannot be successfully read from the
     *     file-system.
     */
    public static Mask openMask(
            StackReader stackReader,
            Path path,
            BinaryValuesInt binaryValues,
            OperationContext context)
            throws ImageIOException {

        Stack stack = stackReader.readStack(path, context);

        if (stack.getNumberChannels() != 1) {
            throw new ImageIOException(
                    String.format(
                            "There must be exactly one channel, but there are %d",
                            stack.getNumberChannels()));
        }

        return new Mask(stack.getChannel(0), binaryValues);
    }
}
