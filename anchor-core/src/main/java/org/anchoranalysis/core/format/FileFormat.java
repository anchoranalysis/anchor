/*-
 * #%L
 * anchor-core
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
package org.anchoranalysis.core.format;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A file-format that is read or written to the filesystem.
 *
 * @author Owen Feehan
 */
public interface FileFormat {

    /**
     * Does a filePath (or fileName) have an extension expected by this format?
     *
     * <p>The case of the extension is irrelevant i.e. a case insensitive match occurs.
     *
     * @param filePath the path
     * @return true if the path ends with any of the expected extensions for this format.
     */
    default boolean matches(Path filePath) {
        return matches(filePath.toString());
    }

    /**
     * Does a file-path (or file-name) <b>end</b> with an extension expected by this format?
     *
     * <p>The case of the extension is irrelevant i.e. a case insensitive match occurs.
     *
     * <p>A leading period is also expected before the extension.
     *
     * @param filePath the path
     * @return true if the path ends with any of the expected extensions for this format.
     */
    boolean matches(String filePath);

    /**
     * Does an identifier <b>equal</b> the extension expected by this format?
     *
     * @param identifier an identifier that should be equal (ignoring case, and with or without a
     *     leading period) to any extension associated with the format
     * @return true if {@code identifier} is equal to an extension associated with this format
     */
    boolean matchesIdentifier(String identifier);

    /**
     * An identifier that describes the format, that is meaningful to the end-user.
     *
     * @return a short string that identifies the format (usually the default extension without any
     *     leading period).
     */
    String descriptiveIdentifier();

    /**
     * The default extension to write associated with a particular format.
     *
     * @return the extension
     */
    String getDefaultExtension();

    /**
     * Builds a path which has an existing path but missing an extension.
     *
     * @param path the path
     * @return the path with an extension appended.
     */
    default String buildPath(String path) {
        return path + "." + getDefaultExtension();
    }

    /**
     * Builds a path which has an existing path but missing an extension.
     *
     * @param path the path
     * @return the path with an extension appended.
     */
    default Path buildPath(Path path) {
        return Paths.get(buildPath(path.toString()));
    }

    /**
     * Builds a path with a directory and filename component.
     *
     * @param directory the directory
     * @param filenameWithoutExtension a filename that resides in the directory (without any
     *     extension or the period preceding an extension)
     * @return a path that combines the {@code directory} and {@code filenameWithoutExtension} and
     *     the extension.
     */
    default Path buildPath(Path directory, String filenameWithoutExtension) {
        return directory.resolve(filenameWithoutExtension + "." + getDefaultExtension());
    }

    /**
     * Builds a path with a directory and filename component.
     *
     * @param directory the directory
     * @param filenameWithoutExtension a filename that resides in the directory (without any
     *     extension or the period preceding an extension)
     * @return a string representing a path that combines the {@code directory} and {@code
     *     filenameWithoutExtension} and the extension.
     */
    default String buildPath(String directory, String filenameWithoutExtension) {
        return String.format(
                "%s/%s.%s", directory, filenameWithoutExtension, getDefaultExtension());
    }
}
