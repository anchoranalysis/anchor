package org.anchoranalysis.spatial.rtree;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

/**
 * Asserts different aspects of an r-tree.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class RTreeChecker {

    /** The r-tree freshly created and intialized for each test. */
    private RTree<Integer> rTree;

    /** Asserts the total size of the r-tree is as expected. */
    public void assertSize(int expectedSize) {
        assertEquals(expectedSize, rTree.size());
    }

    /** Asserts a {@link List} is equal to another, disregarding the order of elements. */
    public static void assertUnordered(List<Integer> expected, Set<Integer> actual) {
        Set<Integer> expectedAsSet = expected.stream().collect(Collectors.toSet());
        assertEquals(expectedAsSet, actual);
    }
}
