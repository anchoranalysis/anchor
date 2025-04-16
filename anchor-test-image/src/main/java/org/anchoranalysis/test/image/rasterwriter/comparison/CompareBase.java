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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import lombok.AllArgsConstructor;
import org.anchoranalysis.test.image.DualComparer;

@AllArgsConstructor
abstract class CompareBase implements ImageComparer {

    protected DualComparer comparer;

    /**
     * Asserts that two image-files being compared are identical as images.
     *
     * <p>It's unspecified how exactly images should be opened from the information supplied. This
     * is implementation-dependent.
     *
     * @param filenameWithoutExtension the filename before extensions were added.
     * @param filenameWithExtension the filename with an extension added.
     * @param path a path to be displayed if an error occurs.
     * @throws IOException if either of files being compared cannot be successfully opened.
     */
    public void assertIdentical(
            String filenameWithoutExtension, String filenameWithExtension, Path path)
            throws IOException {

        try {
            String assertMessage = filenameWithoutExtension + "_" + identifierForTest();
            assertTrue(
                    areIdentical(filenameWithoutExtension, filenameWithExtension), assertMessage);
        } catch (IOException e) {
            System.err.printf( // NOSONAR
                    "The test wrote a file to temporary-folder directory at:%n%s%n", path);
            throw new IOException(
                    String.format(
                            "The comparer threw an IOException, which likely means it cannot find an appropriate raster to compare against for %s.",
                            filenameWithoutExtension),
                    e);
        }
    }

    /**
     * Compares two image files to determine if they are identical.
     *
     * @param filenameWithoutExtension the filename before extensions were added
     * @param filenameWithExtension the filename with an extension added
     * @return true if the images are identical, false otherwise
     * @throws IOException if either of the files being compared cannot be successfully opened
     */
    protected abstract boolean areIdentical(
            String filenameWithoutExtension, String filenameWithExtension) throws IOException;

    /**
     * Provides a unique identifier for this comparison method.
     *
     * @return a string that uniquely identifies this comparison method in test assertions
     */
    protected abstract String identifierForTest();
}
