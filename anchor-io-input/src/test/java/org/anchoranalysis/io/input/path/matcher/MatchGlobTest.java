package org.anchoranalysis.io.input.path.matcher;

import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.input.bean.path.matcher.MatchGlob;
import org.anchoranalysis.test.TestLoader;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link MatchGlob}.
 *
 * @author Owen Feehan
 */
class MatchGlobTest {

    private FilePathMatcherFixture fixture =
            new FilePathMatcherFixture(TestLoader.createFromMavenWorkingDirectory());

    @Test
    void testSingleWildcard() throws InputReadFailedException {
        doTest(2, 2, "*.txt");
    }

    @Test
    void testDoubleWildcard() throws InputReadFailedException {
        doTest(2, 6, "**.txt");
    }

    @Test
    void testFilesWithDotSingle() throws InputReadFailedException {
        doTest(3, 3, "*.*");
    }

    @Test
    void testFilesWithDotDouble() throws InputReadFailedException {
        doTest(3, 9, "**.*");
    }

    private void doTest(int expectedNumberNonRecursive, int expectedNumberRecursive, String glob)
            throws InputReadFailedException {
        MatchGlob matcher = new MatchGlob();
        matcher.setGlob(glob);
        fixture.doTest(expectedNumberNonRecursive, expectedNumberRecursive, matcher, "");
    }
}
