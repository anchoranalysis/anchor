/*-
 * #%L
 * anchor-io-input
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.io.input.path.matcher;

import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.input.bean.path.matcher.MatchExtensions;
import org.anchoranalysis.test.TestLoader;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link MatchExtensions}.
 *
 * <p>Two extensions are checked in different combinations: {@value MatchExtensionsTest#EXTENSION1}
 * and {@value MatchExtensionsTest#EXTENSION2}.
 *
 * @author Owen Feehan
 */
class MatchExtensionsTest {

    /** Some files with this extension exists in the resources directory. */
    private static final String EXTENSION1 = "txt";

    /** Some files with this extension exists in the resources directory. */
    private static final String EXTENSION2 = "tx2";

    /** No file with this extension exists in the resources directory. */
    private static final String EXTENSION_NOT_EXISTING = "jpg";

    private FilePathMatcherFixture fixture =
            new FilePathMatcherFixture(TestLoader.createFromMavenWorkingDirectory());

    @Test
    void test1Only() throws InputReadFailedException {
        doTest(2, EXTENSION1);
    }

    @Test
    void test2Only() throws InputReadFailedException {
        doTest(1, EXTENSION2);
    }

    @Test
    void testBoth() throws InputReadFailedException {
        doTest(3, EXTENSION1, EXTENSION2);
    }

    @Test
    void testNotExisting() throws InputReadFailedException {
        doTest(0, EXTENSION_NOT_EXISTING);
    }

    private void doTest(int expectedNumberNonRecursive, String... extensions)
            throws InputReadFailedException {
        MatchExtensions matcher = new MatchExtensions(extensions);

        // Due to the particular files in the resources directory, we know there are always 3 times
        // more when
        // searched recursively than just in the directory root.
        fixture.doTest(expectedNumberNonRecursive, expectedNumberNonRecursive * 3, matcher, "");
    }
}
