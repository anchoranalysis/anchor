/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.core.value;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.OptionalUtilities;

/**
 * Utility functions for manipualting strings.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtilities {

    /**
     * Like {@link String#join} but ignores any empty strings, and returns {@link Optional#empty} if
     * all components are empty.
     *
     * @param delimeter delimeter to use when joining strings
     * @param stringMaybeEmpty strings that may or may not be empty.
     * @return a joined string including only the non-empty strings, and with a delimeter between
     *     them.
     */
    public static Optional<String> joinNonEmpty(String delimeter, String... stringMaybeEmpty) {
        List<String> components = listOfNonEmptyStrings(stringMaybeEmpty);
        return OptionalUtilities.createFromFlag(
                !components.isEmpty(), () -> String.join(delimeter, components));
    }

    /** Creates a list of strings, where strings are included only if they are non-empty. */
    private static List<String> listOfNonEmptyStrings(String... stringMaybeEmpty) {
        return Arrays.stream(stringMaybeEmpty)
                .filter(string -> !string.isEmpty())
                .collect(Collectors.toList());
    }
}
