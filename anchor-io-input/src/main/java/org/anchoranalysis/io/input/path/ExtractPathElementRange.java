/*-
 * #%L
 * anchor-io-input
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.io.input.path;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.index.range.IndexRangeNegative;

/**
 * Extracts a range of elements from a {@link Path}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExtractPathElementRange {

    /**
     * Extracts a sub-path from a {@link Path} by only retaining a range of elements.
     *
     * <p>The name-elements are a split of the {@link Path} by the directory-separator. See the
     * {@link Path} Javadoc.
     *
     * <p>All indices begin at 0 (for the first element), and can also accept negative-indices which
     * count backwards from the end.
     *
     * <p>e.g. -1 is the last element; -2 is the second-last element.
     *
     * @param path the path to find a sub-path for.
     * @param range which elements to include or not.
     * @return a newly created {@link Path} containing only the elements of {@code path} in the
     *     sub-range.
     * @throws DerivePathException if the range contains invalid indices.
     */
    public static Path extract(Path path, IndexRangeNegative range) throws DerivePathException {
        try {
            List<Path> elementsSubrange = range.extract(path.getNameCount(), path::getName);
            if (!elementsSubrange.isEmpty()) {
                return getFile(elementsSubrange).toPath();
            } else {
                throw new DerivePathException(
                        String.format(
                                "No element exists in the range %s for path %s:", range, path));
            }
        } catch (OperationFailedException e) {
            throw new DerivePathException(
                    "Cannot extract a subrange of elements from the path: " + path, e);
        }
    }

    /** Construct a file from a collection of name elements. */
    private static File getFile(Collection<Path> elements) throws DerivePathException {
        File file = null;
        for (final Path name : elements) {
            if (file == null) {
                file = new File(name.toString());
            } else {
                file = new File(file, name.toString());
            }
        }
        if (file != null) {
            return file;
        } else {
            throw new DerivePathException(
                    "Zero paths elements exist in the range, so a file cannot be constructed.");
        }
    }
}
