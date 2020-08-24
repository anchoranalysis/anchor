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

package org.anchoranalysis.io.filepath.prefixer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.filepath.FilePathToUnixStyleConverter;

/**
 * Calculates the "difference" between a path and a base
 *
 * <p>i.e. if a base is <code>c:\root\somePrefix_</code> and a file is <code>
 * c:\root\somePrefix_someFile.xml</code> then the difference is <code>_someFile.xml</code>
 *
 * <p>The difference is recorded separately as folder and filename components
 *
 * <p>Internally, both paths are converted to absolute paths and URIs.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PathDifferenceFromBase {

    @Getter private final String filename;

    @Getter private final Optional<Path> folder;

    /**
     * Finds the difference between a path and a base
     *
     * @param baseFolderPath path to a base folder
     * @param filePath the path to resolve
     * @throws AnchorIOException if the canonical file cannot be found
     */
    public static PathDifferenceFromBase differenceFrom(Path baseFolderPath, Path filePath)
            throws AnchorIOException {

        try {
            String base = baseFolderPath.toFile().getCanonicalFile().toURI().getPath();
            String all = filePath.toFile().getCanonicalFile().toURI().getPath();

            // As we've converted to URIs the seperator is always a forward slash
            return calculateDifference(base, all);
        } catch (IOException e) {
            throw new AnchorIOException("Cannot fully resolve paths");
        }
    }

    /**
     * Performs the difference
     *
     * <p>Assumes base is a folder. Relies on this.
     *
     * @param baseFolderPath the base-folder as a string
     * @param entirePath the entire path as a string
     */
    private static PathDifferenceFromBase calculateDifference(String baseFolderPath, String entirePath) {

        // Convert the base, and all to forward slashes only
        String base = FilePathToUnixStyleConverter.toStringUnixStyle(baseFolderPath);
        String all = FilePathToUnixStyleConverter.toStringUnixStyle(entirePath);

        // if base is non-empty, but doesn't end in a directory separator we add one
        //  (we use a forward slash due to the previous step converting it into a URL style)
        if (!base.isEmpty() && !base.endsWith("/")) {
            base = base.concat("/");
        }

        // We cannot match the base against the entire string
        if (!all.startsWith(base)) {
            throw new IllegalArgumentException(
                    String.format("Cannot match base '%s' against '%s'", base, all));
        }

        // Remainder path
        return differenceFromRemainder(all.substring(base.length()));
    }

    /**
     * The folder (if it exists) and filename combined.
     *
     * @return the combined-path
     */
    public Path combined() {
        if (folder.isPresent()) {
            return folder.get().resolve(getFilename());
        } else {
            return Paths.get(getFilename());
        }
    }
        
    private static PathDifferenceFromBase differenceFromRemainder(String remainder) {
        File remainderFile = new File(remainder);

        return new PathDifferenceFromBase(
                remainderFile.getName(),
                Optional.ofNullable(remainderFile.getParentFile()).map(File::toPath));        
    }
}
