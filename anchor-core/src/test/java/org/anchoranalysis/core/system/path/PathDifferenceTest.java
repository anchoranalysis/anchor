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

package org.anchoranalysis.core.system.path;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.junit.Test;

public class PathDifferenceTest {

    @Test
    public void testDirectoryAbsoluteWindowsDirectory() throws PathDifferenceException {
        test(
                "c:\\somebase\\",
                "c:\\somebase\\someDir1\\someDir2\\someFile1.txt",
                "someDir1\\someDir2",
                "someFile1.txt");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDirectoryAbsoluteWindowsNonDirectoryBase() throws PathDifferenceException {
        test("c:\\somebase\\base_", "c:\\somebase\\base_someDir1\\someDir2\\someFile1.txt");
    }

    @Test
    public void testDirectoryAbsoluteUnixDirectory() throws PathDifferenceException {
        test(
                "/somebase/",
                "/somebase/someDir1/someDir2/someFile1.txt",
                "someDir1/someDir2",
                "someFile1.txt");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDirectoryAbsoluteUnixNonDirectoryBas() throws PathDifferenceException {
        test("/somebase/extra_", "/somebase/someDir1/someDir2/someFile1.txt");
    }

    @Test
    public void testDirectoryRelativeWindowsDirectory() throws PathDifferenceException {
        test(
                "..\\somebase\\",
                "..\\somebase\\someDir1\\someDir2\\someFile1.txt",
                "someDir1\\someDir2",
                "someFile1.txt");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDirectoryRelativeWindowsNonDirectoryBase() throws PathDifferenceException {
        test("..\\somebase\\base_", "..\\somebase\\base_someDir1\\someDir2\\someFile1.txt");
    }

    @Test
    public void testDirectoryRelativeUnixDirectory() throws PathDifferenceException {
        test(
                "../somebase/",
                "../somebase/someDir1/someDir2/someFile1.txt",
                "someDir1/someDir2",
                "someFile1.txt");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDirectoryRelativeUnixNonDirectoryBase() throws PathDifferenceException {
        test("../somebase/extra_", "../somebase/someDir1/someDir2/someFile1.txt");
    }

    private void test(
            String baseDirectoryPath,
            String filePath,
            String expectedDirectory,
            String expectedFilename)
            throws PathDifferenceException {
        PathDifference difference = test(baseDirectoryPath, filePath);
        assertEquals(Optional.of(resolve(expectedDirectory)), difference.getDirectory());
        assertEquals(expectedFilename, difference.getFilename());
    }

    private PathDifference test(String baseDirectoryPath, String filePath)
            throws PathDifferenceException {
        return PathDifference.differenceFrom(resolve(baseDirectoryPath), resolve(filePath));
    }

    private Path resolve(String path) {
        // We treat it as a UNIX path so the tests will work on all platforms
        return Paths.get(FilePathToUnixStyleConverter.toStringUnixStyle(path));
    }
}
