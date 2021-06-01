package org.anchoranalysis.core.index;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.core.exception.OperationFailedException;

/**
 * Specifying a sub-range of indices.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@Value
public class IndexRange {

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
     * Extracts a subset of elements from a list according to the specified index/range.
     *
     * @param <T> type of element in the list
     * @param list the list to extract a subset from
     * @return a newly created list that contains a subset of elements from {@code list}
     *     corresponding to the indec/range.
     * @throws OperationFailedException if an index lies outside the range {@code (-listSize,
     *     listSize)} or {@code startIndex>=endIndex}.
     */
    public <T> List<T> extract(List<T> list) throws OperationFailedException {
        int listSize = list.size();
        int startCorrected = correctNegative(startIndex, listSize);
        int endCorrected = correctNegative(endIndex, listSize);

        if (startCorrected > endCorrected) {
            throw new OperationFailedException(
                    String.format(
                            "The start-index %d must produce an earlier position than the end-index %d.",
                            startIndex, endIndex));
        }

        return list.subList(startCorrected, endCorrected + 1);
    }

    /**
     * Changes a negative number (within bounds) to a positive number, by adding in the size of the
     * list.
     */
    private static int correctNegative(int index, int listSize) throws OperationFailedException {
        int indexToCorrect = index;
        if (indexToCorrect < 0) {
            indexToCorrect = indexToCorrect + listSize;
        } else {
            if (indexToCorrect >= listSize) {
                throw createException(index, listSize);
            }
        }
        if (indexToCorrect < 0) {
            throw createException(index, listSize);
        }
        return indexToCorrect;
    }

    private static OperationFailedException createException(int index, int listSize) {
        return new OperationFailedException(
                String.format("Index %d is invalid for a list with size %d.", index, listSize));
    }
}
