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
package org.anchoranalysis.test.image.rasterwriter;

import java.nio.file.Path;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.test.TestLoader;
import org.anchoranalysis.test.image.DualComparer;
import org.anchoranalysis.test.image.DualComparerFactory;

/**
 * Access previously saved-files in resources.
 *
 * <p>Different sets of saved-files exist, indexed by their file {@code extension}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SavedFiles {

    private static final String SAVED_FILES_IN_RESOURCES_DIRECTORY = "stackWriter/formats/";

    /**
     * Creates a comparer between a directory and saved-files in a directory in the resources.
     *
     * @param directory the directory (forms the first element to compare).
     * @param extension a file-extension to identify a set of saved-files (forms the second element
     *     to compare).
     * @return a newly created comparer.
     */
    public static DualComparer createComparer(Path directory, String extension) {
        return DualComparerFactory.compareTemporaryDirectoryToTest(
                directory, Optional.empty(), relativeResourcesDirectory(extension));
    }

    /**
     * Creates a loader for reading from a particular set of saved-files in a directory in the
     * resources.
     *
     * @param extension a file-extension to identify a set of saved-files (forms the second element
     *     to compare).
     * @return a newly created loader
     */
    public static TestLoader createLoader(String extension) {
        return TestLoader.createFromMavenWorkingDirectory(relativeResourcesDirectory(extension));
    }

    private static String relativeResourcesDirectory(String extension) {
        return SAVED_FILES_IN_RESOURCES_DIRECTORY + extension;
    }
}
