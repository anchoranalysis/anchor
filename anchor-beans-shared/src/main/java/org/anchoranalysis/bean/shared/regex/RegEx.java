/*-
 * #%L
 * anchor-beans-shared
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

package org.anchoranalysis.bean.shared.regex;

import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;

/**
 * The regular-expression to use for matching groups.
 *
 * <p>It should always define at least one group (i.e. using parentheses).
 *
 * @author Owen Feehan
 */
public abstract class RegEx extends AnchorBean<RegEx> {

    /**
     * Returns an array of string components it matches successfully, or {@link Optional#empty()} if
     * it cannot match.
     *
     * @param string string to match against the regular-expression.
     * @return an array of string components representing each group in the match, or {@link
     *     Optional#empty()} if no match is possible.
     */
    public abstract Optional<String[]> match(String string);

    /**
     * Returns a boolean whether the regular-expression successfully matches a string or not.
     *
     * @param string string to match against the regular-expression.
     * @return true if successfully matched, false otherwise.
     */
    public boolean hasMatch(String string) {
        return match(string).isPresent();
    }
}
