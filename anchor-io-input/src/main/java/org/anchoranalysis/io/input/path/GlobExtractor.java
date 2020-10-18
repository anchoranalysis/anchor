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

package org.anchoranalysis.io.input.path;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.anchoranalysis.io.input.bean.files.SingleFile;

/**
 * Extracts a glob from a string describing it, and a direcory which gives it context.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GlobExtractor {

    @Value
    @AllArgsConstructor
    public static class GlobWithDirectory {

        /** The directory part of the string, or null if it doesn't exist */
        private Optional<String> directory;

        private String glob;
    }

    /**
     * Extracts a glob, and a directory portion if it exists from a string with a wildcard
     *
     * <p>Note that any back-slashes will be converted to forward-slashes.
     *
     * @param wildcardStr a string containing a wildcard
     * @return a GlobWithDirectory where the directory is null if it doesn't exist
     */
    public static GlobWithDirectory extract(String wildcardStr) {
        String str = SingleFile.replaceBackslashes(wildcardStr);

        int finalSlash = positionFinalSlashBeforeWildcard(str);

        if (finalSlash == -1) {
            return new GlobWithDirectory(Optional.empty(), str);
        } else {
            return new GlobWithDirectory(
                    Optional.of(str.substring(0, finalSlash + 1)), str.substring(finalSlash + 1));
        }
    }

    private static int positionFinalSlashBeforeWildcard(String withWildcard) {
        int firstWildcardPos = withWildcard.indexOf('*');
        String without = withWildcard.substring(0, firstWildcardPos);
        return without.lastIndexOf('/');
    }
}
