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
            TestLoader testLoader, String subdirectory1, String subdirectory2) {
        return new DualComparer(
                testLoader.createForSubdirectory(subdirectory1),
                testLoader.createForSubdirectory(subdirectory2));
    }

    public static DualComparer compareTwoSubdirectoriesInLoader(
            TestLoader testLoader1,
            String subdirectory1,
            TestLoader testLoader2,
            String subdirectory2) {
        return new DualComparer(
                testLoader1.createForSubdirectory(subdirectory1),
                testLoader2.createForSubdirectory(subdirectory2));
    }

    private static TestLoader loaderTemporaryFolder(
            TemporaryFolder folder, String relativeTemporaryFolder) {
        Path pathTemporary = Paths.get(folder.getRoot().getAbsolutePath(), relativeTemporaryFolder);
        return TestLoader.createFromExplicitDirectory(pathTemporary);
    }
}
