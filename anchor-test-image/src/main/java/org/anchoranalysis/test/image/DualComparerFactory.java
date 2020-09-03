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
package org.anchoranalysis.test.image;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.test.TestLoader;
import org.junit.rules.TemporaryFolder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DualComparerFactory {

    public static DualComparer compareExplicitFolderToTest(
            Path pathExplicit, String pathRelativeTestDir) {

        TestLoader loaderTemporary = TestLoader.createFromExplicitDirectory(pathExplicit);
        TestLoader loaderTest = TestLoader.createFromMavenWorkingDirectory(pathRelativeTestDir);

        return new DualComparer(loaderTemporary, loaderTest);
    }

    public static DualComparer compareTemporaryFolderToTest(
            TemporaryFolder folder, String relativeTemporaryFolder, String pathRelativeTestDir) {

        TestLoader loaderTemporary = loaderTemporaryFolder(folder, relativeTemporaryFolder);
        TestLoader loaderTest = TestLoader.createFromMavenWorkingDirectory(pathRelativeTestDir);

        return new DualComparer(loaderTemporary, loaderTest);
    }

    public static DualComparer compareTwoSubdirectoriesInLoader(
            TestLoader loader, String subdirectory1, String subdirectory2) {
        return new DualComparer(
                loader.createForSubdirectory(subdirectory1),
                loader.createForSubdirectory(subdirectory2));
    }

    public static DualComparer compareTwoSubdirectoriesInLoader(
            TestLoader loader1,
            String subdirectory1,
            TestLoader loader2,
            String subdirectory2) {
        return new DualComparer(
                loader1.createForSubdirectory(subdirectory1),
                loader2.createForSubdirectory(subdirectory2));
    }

    private static TestLoader loaderTemporaryFolder(
            TemporaryFolder folder, String relativeTemporaryFolder) {
        Path pathTemporary = Paths.get(folder.getRoot().getAbsolutePath(), relativeTemporaryFolder);
        return TestLoader.createFromExplicitDirectory(pathTemporary);
    }
}
