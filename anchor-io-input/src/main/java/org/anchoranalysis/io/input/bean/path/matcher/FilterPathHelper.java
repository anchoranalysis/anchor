/*-
 * #%L
 * anchor-io
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

package org.anchoranalysis.io.input.bean.path.matcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.checked.CheckedPredicate;
import org.anchoranalysis.core.system.path.PathDifference;
import org.anchoranalysis.core.system.path.PathDifferenceException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class FilterPathHelper {

    public static CheckedPredicate<Path, IOException> createPredicate(
            Path directory, String filterType, String fileFilter) {
        PathMatcher matcher =
                directory.getFileSystem().getPathMatcher(filterType + ":" + fileFilter);
        return path -> FilterPathHelper.testPathOnDifference(path, directory, matcher);
    }

    private static boolean testPathOnDifference(Path path, Path directory, PathMatcher matcher)
            throws IOException {
        try {
            PathDifference difference = PathDifference.differenceFrom(directory, path);
            return matcher.matches(difference.combined());
        } catch (PathDifferenceException e) {
            throw new IOException(e);
        }
    }
}
