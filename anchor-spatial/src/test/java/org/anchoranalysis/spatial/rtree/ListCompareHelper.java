package org.anchoranalysis.spatial.rtree;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Compares two lists, disregarding order of elements.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ListCompareHelper {

    /**
     * Does the list contain the same elements as a Set?
     *
     * @param list list to compare
     * @param set set to compare
     * @return true if the list and set have identical elements
     */
    public static <T> boolean equalsUnordered(List<T> list, Set<T> set) {
        if (list.size() != set.size()) {
            return false;
        }
        return toSet(list).equals(set);
    }

    /** Converts a {@link List} to a {@link Set}. */
    private static <T> Set<T> toSet(List<T> list) {
        return list.stream().collect(Collectors.toSet());
    }
}
