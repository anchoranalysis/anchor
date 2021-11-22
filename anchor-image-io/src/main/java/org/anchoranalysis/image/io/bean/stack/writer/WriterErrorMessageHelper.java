/*-
 * #%L
 * anchor-image-io
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.io.ImageIOException;

/**
 * Helps create similar error messages among {@link StackWriter}s.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WriterErrorMessageHelper {

    /**
     * Creates a {@link ImageIOException} with an error message that image-writing failed.
     *
     * @param <T> the writer in which the writing failed.
     * @param writerClass class corresponding to {@code <T>}.
     * @param path the path that the image was been written to, when failure occurred.
     * @param cause the cause of failure.
     * @return an exception that can be thrown describing the failure.
     */
    public static <T extends StackWriter> ImageIOException generalWriteException(
            Class<T> writerClass, Path path, Exception cause) {
        String message =
                String.format(
                        "An error occurred writing an image to %s using the %s",
                        path, writerClass.getSimpleName());
        return new ImageIOException(message, cause);
    }
}
