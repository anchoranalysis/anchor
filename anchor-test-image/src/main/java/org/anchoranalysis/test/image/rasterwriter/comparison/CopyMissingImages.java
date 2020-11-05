/*-
 * #%L
 * anchor-test-image
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
package org.anchoranalysis.test.image.rasterwriter.comparison;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.AllArgsConstructor;

/**
 * When an error is thrown by a comparison, the generated raster at {@code path} is coped to a
 * destination fodler.
 *
 * <p>This is useful for building a set of rasters to compare against, when they don't already
 * exist.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class CopyMissingImages implements ImageComparer {

    /** The comparer that does the comparison. */
    private ImageComparer comparer;

    /** A path to a directory to copy the missing images to. */
    private Path directoryToCopyTo;

    @Override
    public void assertIdentical(
            String filenameWithoutExtension, String filenameWithExtension, Path path)
            throws IOException {
        try {
            comparer.assertIdentical(filenameWithoutExtension, filenameWithExtension, path);
        } catch (IOException e) {
            if (directoryToCopyTo.toFile().isDirectory()) {
                Files.copy(path, directoryToCopyTo.resolve(filenameWithExtension));
            } else {
                throw new IOException(
                        "directoryToCopyTo does not specify a path to a directory: "
                                + directoryToCopyTo);
            }
        }
    }
}
