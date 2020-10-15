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

import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.path.FilePathToUnixStyleConverter;
import org.anchoranalysis.io.input.InputContextParams;

/**
 * Predicates that matches a file-path against a regular expression.
 *
 * @author Owen Feehan
 */
public class MatchRegularExpression extends PathMatcher {

    // START BEAN FIELDS
    /**
     * If true, the filter is applied to the path as a whole, not just the filename (using forward
     * slashes as directory separators)
     */
    @BeanField @Getter @Setter private boolean applyToPath = false;

    @BeanField @Getter @Setter private String expression;
    // END BEAN FIELDS

    @Override
    protected Predicate<Path> createMatcherFile(Path directory, InputContextParams inputContext) {
        if (applyToPath) {
            Pattern pattern = Pattern.compile(expression);
            return path -> acceptPathViaRegEx(path, pattern);
        } else {
            return FilterPathHelper.createPredicate(directory, "regex", expression);
        }
    }

    public static boolean acceptPathViaRegEx(Path path, Pattern pattern) {
        return pattern.matcher(pathAsString(path)).matches();
    }

    private static String pathAsString(Path path) {
        return FilePathToUnixStyleConverter.toStringUnixStyle(path.toFile().toString());
    }
}
