/*-
 * #%L
 * anchor-io-manifest
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

package org.anchoranalysis.io.manifest.finder.match;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestDirectoryDescription;
import org.anchoranalysis.io.manifest.directory.JobRootDirectory;
import org.anchoranalysis.io.manifest.directory.MutableDirectory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DirectoryMatch {

    public static Predicate<MutableDirectory> jobDirectory() {
        return JobRootDirectory.class::isInstance;
    }

    public static Predicate<MutableDirectory> path(String path) {
        Path pathAsPath = Paths.get(path);
        return directory -> directory.relativePath().equals(pathAsPath);
    }

    // If match is null, we match everything
    public static Predicate<MutableDirectory> description(String function, String type) {
        return directory ->
                DirectoryMatch.presentAnd(
                        directory.description(), DescriptionMatch.functionAndType(function, type));
    }

    private static boolean presentAnd(
            ManifestDirectoryDescription descriptionToTest,
            Predicate<ManifestDescription> descriptionMatch) {
        return descriptionToTest.getDescription().isPresent()
                && descriptionMatch.test(descriptionToTest.getDescription().get()); // NOSONAR
    }
}
