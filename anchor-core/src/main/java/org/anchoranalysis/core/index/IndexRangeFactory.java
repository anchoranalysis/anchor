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
package org.anchoranalysis.core.index;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;

/**
 * Creates {@link IndexRange}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IndexRangeFactory {

    /**
     * Parses a string to convert it into an {@link IndexRange}.
     *
     * <p>The string should be in any of the following formats:
     *
     * <ul>
     *   <li>{@code start:end}
     *   <li>{@code single_index}
     *   <li>{@code :end}
     *   <li>{@code start:}
     * </ul>
     *
     * where
     *
     * <ul>
     *   <li>{@code start} is a positive or negative integer indicating the <b>start index</b> of
     *       the range (where 0 is the first element).
     *   <li>{@code end} is a positive or negative integer indicating the <b>end index</b> of the
     *       range (inclusive, where either -1 or length(array) - 1 is the last element).
     *   <li>{@code single_index} implies the same value for both {@code start} and {@code end}.
     * </ul>
     *
     * @param str the string to parse
     * @return the range extracted from the parsed string
     * @throws OperationFailedException if the string does not match any of the patterns above.
     */
    public static IndexRange parse(String str) throws OperationFailedException {
        if (str.contains(":")) {
            String[] components = str.split(":");
            if (components.length > 2) {
                throw new OperationFailedException("A maximum of one : (colon) is permitted.");
            } else if (components.length == 2) {
                return parseTwoComponents(components[0], components[1]);
            } else if (components.length == 1) {
                return parseSingleComponent(str, components[0]);
            } else {
                throw new AnchorImpossibleSituationException();
            }
        } else {
            return parseStringWithoutColon(str);
        }
    }

    /** If there is a colon surrounded by text on both sides. */
    private static IndexRange parseTwoComponents(String left, String right)
            throws OperationFailedException {
        if (left.isEmpty()) {
            return parseStringAfterColon(right);
        } else if (right.isEmpty()) {
            return parseStringWithoutColon(left);
        } else {
            return new IndexRange(parseAsInt(left), parseAsInt(right));
        }
    }

    /** If there is a colon with text only on one side. */
    private static IndexRange parseSingleComponent(String str, String component)
            throws OperationFailedException {
        if (str.startsWith(":")) {
            return parseStringAfterColon(component);
        } else {
            return parseStringWithoutColon(component);
        }
    }

    /** If there is no colon in the string. */
    private static IndexRange parseStringWithoutColon(String str) throws OperationFailedException {
        int parsed = parseAsInt(str);
        return new IndexRange(parsed, parsed);
    }

    private static IndexRange parseStringAfterColon(String component)
            throws OperationFailedException {
        int parsed = parseAsInt(component);
        return new IndexRange(0, parsed);
    }

    /**
     * Converts text to an integer only if looks exactly like an integer, otherwise throws an
     * exception.
     */
    private static int parseAsInt(String str) throws OperationFailedException {
        if (str.matches("^-?\\d+$")) {
            return Integer.valueOf(str);
        } else {
            throw new OperationFailedException(String.format("%s is not an integer", str));
        }
    }
}
