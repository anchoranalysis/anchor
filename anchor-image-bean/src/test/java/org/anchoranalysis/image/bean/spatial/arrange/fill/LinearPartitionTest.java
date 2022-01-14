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
        collection.stream().forEach(element -> calculator.add(element));
        return Math.sqrt(calculator.variance());
    }
}
