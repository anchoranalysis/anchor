/*-
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.bean.spatial.arrange.fill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.math.statistics.VarianceCalculatorLong;
import org.junit.jupiter.api.Test;

/**
 * Tests {@code LinearPartition} in {@code org.anchoranalysis.image.bean.spatial.arrange.fill}.
 *
 * @author Owen Feehan
 */
class LinearPartitionTest {

    @Test
    void partitionSequence() throws OperationFailedException {
        doTest(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11), 3, 1.5f);
    }

    @Test
    void partitionBigValue() throws OperationFailedException {
        doTest(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1111), 2, 1111f / 2.0f);
    }

    @Test
    void partitionTooFewValues() {
        assertThrows(
                OperationFailedException.class,
                () -> LinearPartition.partition(Arrays.asList(1, 2, 3, 4, 5, 6), 7));
    }

    @Test
    void partitionInvalidNumberPartitions() {
        assertThrows(
                OperationFailedException.class,
                () -> LinearPartition.partition(Arrays.asList(1, 2), 0));
    }

    /**
     * Creates partitions and performs various checks to determine consistency and a maximum
     * standard-deviation of the partition sums.
     *
     * @throws OperationFailedException if thrown by {@link LinearPartition#partition(List, int)}.
     */
    private static void doTest(
            List<Integer> input, int numberPartitions, float maximumStandardDeviationInclusive)
            throws OperationFailedException {
        List<List<Integer>> partitions = LinearPartition.partition(input, numberPartitions);

        // Check for the expected number of partitions
        assertEquals(numberPartitions, partitions.size(), "Number of partitions");

        // Sum each partition
        List<Integer> partitionSums =
                FunctionalList.mapToList(partitions.stream(), LinearPartitionTest::sum);

        // Check all sum of the partitions is the sum of the input elements
        assertEquals(sum(input), sum(partitionSums), "Sum of all partitions");

        // Check that the standard-deviation is less than a certain amount
        double standardDeviation = standardDeviation(partitionSums);
        assertTrue(
                standardDeviation <= maximumStandardDeviationInclusive,
                String.format(
                        "Standard deviation of partition sums (%f) not is less than (%f)",
                        standardDeviation, maximumStandardDeviationInclusive));
    }

    /** Sums a collection of {@link Integer}s. */
    private static int sum(Collection<Integer> collection) {
        return collection.stream().mapToInt(Integer::intValue).sum();
    }

    /** Determines the standard-deviation from a collection of {@link Integer}s. */
    private static double standardDeviation(Collection<Integer> collection) {
        VarianceCalculatorLong calculator = new VarianceCalculatorLong();
        collection.stream().forEach(calculator::add);
        return Math.sqrt(calculator.variance());
    }
}
