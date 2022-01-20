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
