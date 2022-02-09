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

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.input.bean.path.matcher.FilePathMatcher;
import org.anchoranalysis.io.input.bean.path.matcher.MatchRegularExpression;
import org.anchoranalysis.test.TestLoader;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link MatchRegularExpression}.
 *
 * @author Owen Feehan
 */
class MatchRegularExpressionTest {

    private FilePathMatcherFixture fixture =
            new FilePathMatcherFixture(TestLoader.createFromMavenWorkingDirectory());

    @Test
    void testMatchesNothing() throws OperationFailedException {
        doTest(0, 0, 0, ".*foo");
    }

    @Test
    void testMatchesEverything() throws OperationFailedException {
        doTest(4, 12, 12, ".*");
    }

    @Test
    void testEndsExtension() throws OperationFailedException {
        doTest(2, 6, 6, ".*\\.txt$");
    }

    @Test
    void testSubdirectoryOnly() throws OperationFailedException {
        doTest(0, 0, 8, ".*subdir.*");
    }

    @Test
    void testSubdirectoryForwardSlash() throws OperationFailedException {
        doTest(0, 0, 8, ".*/.*");
    }

    @Test
    void testSubdirectoryBackwardSlash() throws OperationFailedException {
        assertThrows(BeanMisconfiguredException.class, () -> createMatcher(".*\\\\.*", false));
    }

    private void doTest(
            int expectedNumberNonRecursive,
            int expectedNumberRecursiveNotApply,
            int expectedNumberRecursiveApply,
            String expression)
            throws OperationFailedException {
        doTest(
                expectedNumberNonRecursive,
                expectedNumberRecursiveNotApply,
                false,
                expression,
                "notApply_");
        doTest(
                expectedNumberNonRecursive,
                expectedNumberRecursiveApply,
                true,
                expression,
                "apply_");
    }

    private void doTest(
            int expectedNumberNonRecursive,
            int expectedNumberRecursive,
            boolean applyToPath,
            String expression,
            String assertMessagePrefix)
            throws OperationFailedException {
        try {
            fixture.doTest(
                    expectedNumberNonRecursive,
                    expectedNumberRecursive,
                    createMatcher(expression, applyToPath),
                    assertMessagePrefix);
        } catch (InputReadFailedException | BeanMisconfiguredException e) {
            throw new OperationFailedException(e);
        }
    }

    private FilePathMatcher createMatcher(String expression, boolean applyToPath)
            throws BeanMisconfiguredException {
        MatchRegularExpression matcher = new MatchRegularExpression();
        matcher.setExpression(expression);
        matcher.setApplyToPath(applyToPath);
        matcher.checkMisconfigured(new BeanInstanceMap());
        return matcher;
    }
}
