/* (C)2020 */
package org.anchoranalysis.io.filepath.prefixer;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.filepath.FilePathToUnixStyleConverter;
import org.junit.Test;

public class FilePathDifferenceFromFolderPathTest {

    @Test
    public void testFolderAbsoluteWindowsFolder() throws AnchorIOException {
        test(
                "c:\\somebase\\",
                "c:\\somebase\\someDir1\\someDir2\\someFile1.txt",
                "someDir1\\someDir2",
                "someFile1.txt");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFolderAbsoluteWindowsNonFolderBase() throws AnchorIOException {
        test("c:\\somebase\\base_", "c:\\somebase\\base_someDir1\\someDir2\\someFile1.txt");
    }

    @Test
    public void testFolderAbsoluteUnixFolder() throws AnchorIOException {
        test(
                "/somebase/",
                "/somebase/someDir1/someDir2/someFile1.txt",
                "someDir1/someDir2",
                "someFile1.txt");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFolderAbsoluteUnixNonFolderBase() throws AnchorIOException {
        test("/somebase/extra_", "/somebase/someDir1/someDir2/someFile1.txt");
    }

    @Test
    public void testFolderRelativeWindowsFolder() throws AnchorIOException {
        test(
                "..\\somebase\\",
                "..\\somebase\\someDir1\\someDir2\\someFile1.txt",
                "someDir1\\someDir2",
                "someFile1.txt");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFolderRelativeWindowsNonFolderBase() throws AnchorIOException {
        test("..\\somebase\\base_", "..\\somebase\\base_someDir1\\someDir2\\someFile1.txt");
    }

    @Test
    public void testFolderRelativeUnixFolder() throws AnchorIOException {
        test(
                "../somebase/",
                "../somebase/someDir1/someDir2/someFile1.txt",
                "someDir1/someDir2",
                "someFile1.txt");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFolderRelativeUnixNonFolderBase() throws AnchorIOException {
        test("../somebase/extra_", "../somebase/someDir1/someDir2/someFile1.txt");
    }

    private void test(
            String baseFolderPath, String filePath, String expectedFolder, String expectedFilename)
            throws AnchorIOException {
        PathDifferenceFromBase fdd = test(baseFolderPath, filePath);
        assertTrue(fdd.getFolder().equals(Optional.of(resolve(expectedFolder))));
        assertTrue(fdd.getFilename().equals(expectedFilename));
    }

    private PathDifferenceFromBase test(String baseFolderPath, String filePath)
            throws AnchorIOException {
        return PathDifferenceFromBase.differenceFrom(resolve(baseFolderPath), resolve(filePath));
    }

    private Path resolve(String path) {
        // We treat it as a UNIX path so the tests will work on all platforms
        return Paths.get(FilePathToUnixStyleConverter.toStringUnixStyle(path));
    }
}
