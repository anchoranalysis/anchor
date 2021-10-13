/*-
 * #%L
 * anchor-spatial
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

    /** 
     * Asserts the total size of the r-tree is as expected.
     * 
     * @param expectedSize the size expected in the r-tree.
     */
    public void assertSize(int expectedSize) {
        assertEquals(expectedSize, rTree.size());
    }

    /** 
     * Asserts a {@link List} is equal to another, disregarding the order of elements.
     * 
     * @param expected the expected list.
     * @param actual the actual list produced in the test.
     */
    public static void assertUnordered(List<Integer> expected, Set<Integer> actual) {
        Set<Integer> expectedAsSet = expected.stream().collect(Collectors.toSet());
        assertEquals(expectedAsSet, actual);
    }
}
