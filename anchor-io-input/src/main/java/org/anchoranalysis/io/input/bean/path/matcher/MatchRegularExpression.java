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

package org.anchoranalysis.io.input.bean.path.matcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;
import org.anchoranalysis.core.functional.checked.CheckedPredicate;
import org.anchoranalysis.io.input.InputContextParameters;

/**
 * Predicates that matches a file-path against a regular expression.
 *
 * @author Owen Feehan
 */
public class MatchRegularExpression extends FilePathMatcher {

    // START BEAN FIELDS
    /**
     * If true, the filter is applied to the path, not just the filename (using forward slashes as
     * directory separators)
     *
     * <p>By path, we refer to the relative-path to the file being considered, relative to the
     * directory in which the search started.
     *
     * <p>e.g. if a file {@code /a/b/c/d.txt} is encountered, then the regular-expression will match
     * against {@code c/d.txt} when {@code applyToPath==true}, and {@code d.txt} otherwise.
     */
    @BeanField @Getter @Setter private boolean applyToPath = false;

    /**
     * The regular-expression, Java-style as described in {@link Pattern}.
     *
     * <p>Regular expressions should be written to expect a forward-slash as the
     * directory-separator, irrespective of operating system. This guarantees consistent behavior
     * across operating systems.
     *
     * <p>Backslashes are not permitted as directory-seperators, only as escape characters.
     */
    @BeanField @Getter @Setter private String expression;

    // END BEAN FIELDS

    @Override
    public void checkMisconfigured(BeanInstanceMap defaultInstances)
            throws BeanMisconfiguredException {
        super.checkMisconfigured(defaultInstances);
        if (expression.contains("\\\\")) {
            throw new BeanMisconfiguredException(
                    "A double backslash is present in expression, but is not permitted: "
                            + expression);
        }
    }

    @Override
    protected CheckedPredicate<Path, IOException> createMatcherFile(
            Path directory, Optional<InputContextParameters> inputContext) {
        return PredicateFromPathMatcher.create(
                directory, "regex", expressionMatchingSlashes(), !applyToPath);
    }

    /**
     * Convert any forward slashes in the expresison to backward slashes, if backslashes are used by
     * the current operating system.
     */
    private String expressionMatchingSlashes() {
        if (File.separatorChar == '\\') {
            // Ultimately this inserts just one expected backslash into the expression
            // A single backslash needs to be escaped by two-backslashes in a regular-expression
            // A single backslash needs to be escaped by two-backslashes in Java, to write a single
            // backslash in a regular-expression
            // In total, 4 blackslashes (2*2) exist in the literal string to test against a single
            // backslash in
            // the actual underlying string :)
            return expression.replace("/", "\\\\");
        } else {
            return expression;
        }
    }

    @Override
    protected boolean canMatchSubdirectories() {
        return true;
    }
}
