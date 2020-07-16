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
/* (C)2020 */
package org.anchoranalysis.io.bean.file.matcher;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PathMatcherUtilities {

    public static Predicate<Path> filter(Path dir, String fileType, String fileFilter) {
        PathMatcher matcher = PathMatcherUtilities.matcherForFilter(dir, fileType, fileFilter);
        return p -> PathMatcherUtilities.acceptPathViaMatcher(p, matcher);
    }

    private static boolean acceptPathViaMatcher(Path path, PathMatcher matcher) {
        return matcher.matches(path.getFileName());
    }

    private static PathMatcher matcherForFilter(Path dir, String filterType, String fileFilter) {
        return dir.getFileSystem().getPathMatcher(filterType + ":" + fileFilter);
    }
}
