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
package org.anchoranalysis.core.system.path;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Test;

/**
 * Tests {@link CommonPath}.
 *
 * @author Owen Feehan
 */
public class CommonPathTest {

    /** Two absolute-paths with commonality. */
    @Test
    public void testDirectoryAbsolutePath() throws IOException {
        assertCommonPath("/a/b/c/d", "/a/b", Optional.of("/a/b"));
    }

    /** Two relative-paths with commonality. */
    @Test
    public void testDirectoryRelativePath() throws IOException {
        assertCommonPath("a/b/c/d", "a/b", Optional.of("a/b"));
    }

    /** One absolute path and one relative-path - with therefore no commonality. */
    @Test
    public void testDirectoryAbsoluteAndRelativePath() throws IOException {
        assertCommonPath("/a/b/c/d", "a/b", Optional.empty());
    }

    private static void assertCommonPath(
            String path1, String path2, Optional<String> expectedCommonPath) {
        List<File> files = Arrays.asList(file(path1), file(path2));
        assertEquals(expectedCommonPath.map(Paths::get), CommonPath.commonPath(files));
    }

    private static File file(String path) {
        return new File(path);
    }
}
