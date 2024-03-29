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
package org.anchoranalysis.core.index.range;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import lombok.Value;
import org.anchoranalysis.core.exception.OperationFailedException;

/**
 * Specifying a sub-range of indices, tolerating also negative indices.
 *
 * <p>Negative indices, loop around the end of the range.
 *
 * @author Owen Feehan
 */
@Value
public class IndexRangeNegative {

    /**
     * The index if the <b>first element</b> to use in the subsetting range, zero-indexed.
     *
     * <p>This can be negative, in which case it goes backwards from the end of the string e.g.
     * index -2 is the same as {@code size(elements) - 2}.
     */
    private int startIndex;

    /**
     * The index if the <b>last element</b> to use in the subsetting range, inclusive.
     *
     * <p>This can be negative, in which case it goes backwards from the end of the string e.g.
     * index -2 is the same as {@code size(elements) - 2}.
     *
     * <p>Therefore, -1 will use the final element as the last element.
     */
    private int endIndex;

    /**
     * Create with a start and end index.
     *
     * @param startIndex the start-index (which can be negative).
     * @param endIndex the end-index (which can be negative).
     * @throws OperationFailedException if the indices are incorrectly ordered.
     */
    public IndexRangeNegative(int startIndex, int endIndex) throws OperationFailedException {
        this.startIndex = startIndex;
        this.endIndex = endIndex;

        if (startIndex >= 0 && endIndex >= 0 && endIndex < startIndex) {
            throw new OperationFailedException(
                    String.format(
                            "The start-index (%d) must precede the end-index (%d)",
                            startIndex, endIndex));
        }

        if (startIndex < 0 && endIndex < 0 && endIndex < startIndex) {
            throw new OperationFailedException(
                    String.format(
                            "The start-index (%d) must precede the end-index (%d)",
                            startIndex, endIndex));
        }
    }

    /**
     * Extracts a subset of elements from a list according to the specified index/range.
     *
     * @param <T> type of element in the list
     * @param list the list to extract a subset from
     * @return a newly created list that contains a subset of elements from {@code list}
     *     corresponding to the index/range.
     * @throws OperationFailedException if an index lies outside the range {@code (-listSize,
     *     listSize)} or {@code startIndex>=endIndex}.
     */
    public <T> List<T> extract(List<T> list) throws OperationFailedException {
        int listSize = list.size();
        int startCorrected = correctedStartIndex(listSize);
        int endCorrected = correctedEndIndex(listSize);

        if (startCorrected > endCorrected) {
            throw new OperationFailedException(
                    String.format(
                            "The start-index %d must produce an earlier position than the end-index %d.",
                            startIndex, endIndex));
        }

        return list.subList(startCorrected, endCorrected + 1);
    }

    /**
     * Extracts a subset of elements from a collection according to the specified index/range.
     *
     * <p>Collection is any generic structure, with a fixed number of elements, that can be accessed
     * via {@code extractIndex}.
     *
     * @param <T> type of element in the list
     * @param numberElements the number of elements in the collection.
     * @param extractIndex extracts an element at a particular index from the collection.
     *     Zero-indexed.
     * @return a newly created list that contains a subset of elements from the collection
     *     corresponding to the index/range.
     * @throws OperationFailedException if an index lies outside the range {@code (-numberElements,
     *     numberElements)} or {@code startIndex>=endIndex}.
     */
    public <T> List<T> extract(int numberElements, IntFunction<T> extractIndex)
            throws OperationFailedException {
        int startCorrected = correctedStartIndex(numberElements);
        int endCorrected = correctedEndIndex(numberElements);

        if (startCorrected > endCorrected) {
            throw new OperationFailedException(
                    String.format(
                            "The start-index %d must produce an earlier position than the end-index %d.",
                            startIndex, endIndex));
        }

        List<T> out = new ArrayList<>(endCorrected - startCorrected + 1);
        for (int i = startCorrected; i <= endCorrected; i++) {
            out.add(extractIndex.apply(i));
        }
        return out;
    }

    /**
     * The start-index, maybe corrected so that it is no longer negative.
     *
     * @param size the total number of elements
     * @return the maybe-corrected start-index
     * @throws OperationFailedException if {@code startIndex} is invalid for a list of size {@code
     *     size}.
     */
    public int correctedStartIndex(int size) throws OperationFailedException {
        return correctNegative(startIndex, size);
    }

    /**
     * The end-index, maybe corrected so that it is no longer negative.
     *
     * @param size the total number of elements
     * @return the maybe-corrected start-index
     * @throws OperationFailedException if {@code endIndex} is invalid for a list of size {@code
     *     size}.
     */
    public int correctedEndIndex(int size) throws OperationFailedException {
        return correctNegative(endIndex, size);
    }

    /**
     * Changes a negative number (within bounds) to a positive number, by adding in the size of the
     * list.
     */
    private static int correctNegative(int index, int listSize) throws OperationFailedException {
        int toCorrect = index;
        if (toCorrect < 0) {
            toCorrect = toCorrect + listSize;
        } else {
            if (toCorrect >= listSize) {
                throw createException(index, listSize);
            }
        }
        if (toCorrect >= 0) {
            return toCorrect;
        } else {
            throw createException(index, listSize);
        }
    }

    private static OperationFailedException createException(int index, int listSize) {
        return new OperationFailedException(
                String.format("Index %d is invalid for a list with size %d.", index, listSize));
    }
}
