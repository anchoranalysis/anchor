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

package org.anchoranalysis.io.output.path.prefixer;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Converts file-paths between relative and absolute.
 *
 * @author Owen Feehan
 */
public interface PathCreator {

    /**
     * Generates a full path, given the final part (suffix) of a path.
     *
     * <p>All sub-directories are created if needed to ensure it's possible to write to the
     * fullPath.
     *
     * @param suffix the final part of the path, to be added to the prefix
     * @param extension a file extension (without a leading string)
     * @param fallbackSuffix if neither a {@code prefix} is defined nor a {@code suffix}, then this
     *     provides a suffix to use so a file isn't only an extension.
     * @return a complete absolute path with all components (prefix, suffix) etc., including the
     *     leading directory.
     */
    Path makePathAbsolute(
            Optional<String> suffix, Optional<String> extension, String fallbackSuffix);

    /**
     * Extracts a relative-file path, given the final part (suffix) of a path.
     *
     * <p>The path will be relative to the underlying root {@code directory}.
     *
     * <p>This relative-path includes any filename-prefix added by the {@link DirectoryWithPrefix}.
     *
     * @param suffix the final part of the path, to be added to the prefix
     * @return a complete relative path with all components (prefix, suffix) etc., but no leading
     *     directory.
     */
    Path makePathRelative(Path suffix);
}
