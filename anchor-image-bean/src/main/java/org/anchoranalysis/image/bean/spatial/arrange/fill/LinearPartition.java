package org.anchoranalysis.image.bean.spatial.arrange.fill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.ToDoubleFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;

/**
 * Dynamic programming approach to <a href="https://en.wikipedia.org/wiki/Partition_problem">Linear
 * Partition</a> problem.
 *
 * <p>Inspired by:
 *
 * <ul>
 *   <li>Python implementation on <a href="http://stackoverflow.com/a/7942946">Stack Overflow</a>.
 *   <li>Java implementation by by abrie on <a
 *       href="https://gist.github.com/abrie/b962ee399d06ada95e88">GitHub Gist</a>.
 * </ul>
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class LinearPartition {

    /**
     * Distributes each element in {@code list} among {@code numberPartitions} so that the sum of
     * each partition is approximately identical.
     *
     * <p>The order of elements in {@code list} is preserved into the sublist that forms each
     * partition.
     *
     * @param list the list to partition.
     * @param numberPartitions the number of partitions to split into.
     * @return a newly created list of lists, representing each partition.
     * @throws OperationFailedException if there are too few items in {@code list} to be split into
     *     {@code numberPartitions}.
     */
    public static List<List<Integer>> partition(List<Integer> list, int numberPartitions)
            throws OperationFailedException {
        return partition(list, value -> (double) value, numberPartitions);
    }

    /**
     * Distributes each element in {@code list} among {@code numberPartitions} so that the sum of
     * costs for each partition is approximately identical.
     *
     * <p>The order of elements in {@code list} is preserved into the sublist that forms each
     * partition.
     *
     * @param list the list to partition.
     * @param extractCost extracts a cost (what is summed in each partition) from a given element in
     *     {@code list}.
     * @param numberPartitions the number of partitions to split into.
     * @return a newly created list of lists, representing each partition.
     * @throws OperationFailedException if there are too few items in {@code list} to be split into
     *     {@code numberPartitions}.
     */
    public static <T> List<List<T>> partition(
            List<T> list, ToDoubleFunction<T> extractCost, int numberPartitions)
            throws OperationFailedException {

        checkSizes(list.size(), numberPartitions);

        // Exit case with identical number of values and partitions
        if (list.size() == numberPartitions) {
            return Arrays.asList(new ArrayList<>(list));
        }

        int[][] table = buildPartitionTable(list, extractCost, numberPartitions);

        return derivePartitionsFromTable(table, list, numberPartitions - 2);
    }

    /**
     * Throws a {@link OperationFailedException} if invalid or incompatible values exist for {@code
     * numberElements} and {@code numberPartitons}.
     */
    private static void checkSizes(int numberElements, int numberPartitions)
            throws OperationFailedException {
        if (numberPartitions <= 0) {
            throw new OperationFailedException(
                    String.format("The number of partitions of %d is invalid.", numberPartitions));
        }

        if (numberElements < numberPartitions) {
            throw new OperationFailedException(
                    String.format(
                            "There are %d elements in the list, which is too few to split into %d partitions",
                            numberElements, numberPartitions));
        }
    }

    /** Builds a table of costs using dynamic programming. */
    private static <T> int[][] buildPartitionTable(
            List<T> elements, ToDoubleFunction<T> extractCost, int numberPartitions) {

        double[][] costs = new double[elements.size()][numberPartitions];
        int[][] solution = new int[elements.size() - 1][numberPartitions - 1];

        // With a single partition
        for (int i = 0; i < elements.size(); i++) {
            costs[i][0] =
                    extractCost.applyAsDouble(elements.get(i)) + ((i > 0) ? (costs[i - 1][0]) : 0);
        }

        // Set first element for all partitions
        for (int j = 0; j < numberPartitions; j++) {
            costs[0][j] = extractCost.applyAsDouble(elements.get(0));
        }

        // Update subsequent elements
        for (int i = 1; i < elements.size(); i++) {
            for (int j = 1; j < numberPartitions; j++) {
                costs[i][j] = Integer.MAX_VALUE;
                for (int x = 0; x < i; x++) {
                    double positionCost = Math.max(costs[x][j - 1], costs[i][0] - costs[x][0]);
                    if (costs[i][j] > positionCost) {
                        costs[i][j] = positionCost;
                        solution[i - 1][j - 1] = x;
                    }
                }
            }
        }
        return solution;
    }

    /** Derive the partitions from the table of costs. */
    private static <T> LinkedList<List<T>> derivePartitionsFromTable(
            int[][] table, List<T> list, int partitionIndex) {
        LinkedList<List<T>> partitions = new LinkedList<>();
        int elementIndex = list.size() - 1;
        while (partitionIndex >= 0) {
            addPartition(
                    list,
                    table[elementIndex - 1][partitionIndex] + 1,
                    elementIndex + 1,
                    partitions);

            elementIndex = table[elementIndex - 1][partitionIndex];
            partitionIndex = partitionIndex - 1;
        }
        addPartition(list, 0, elementIndex + 1, partitions);
        return partitions;
    }

    /** Extract a sub-range from {@code list} and add it to {@code partitions}. */
    private static <T> void addPartition(
            List<T> elements,
            int startIndexInclusive,
            int endIndexExclusive,
            LinkedList<List<T>> partitions) {
        List<T> partition =
                new ArrayList<>(elements.subList(startIndexInclusive, endIndexExclusive));
        partitions.addFirst(partition);
    }
}
