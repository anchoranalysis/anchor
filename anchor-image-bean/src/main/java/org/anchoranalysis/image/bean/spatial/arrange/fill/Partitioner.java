package org.anchoranalysis.image.bean.spatial.arrange.fill;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;

/**
 * Partitions a list of {@link ExtentToArrange}s into rows.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class Partitioner {

    /**
     * Partitions {@code elements} into smaller lists, each representing a particular row.
     *
     * @param elements the elements to partition.
     * @param numberRows how many rows to partition into.
     * @param varyNumberImagesPerRow if the number of elements in each row may vary (i.e. it must
     *     not be uniform).
     * @return a list of rows, where each row is a subset of elements after partitioned. Each
     *     element in {@code elements} will appear once across these nested lists.
     * @throws OperationFailedException if there are too few items in {@code elements} to be split
     *     into partitions, when {@code varyNumberImagesPerRow==true}.
     */
    public static List<List<ExtentToArrange>> partitionExtents(
            List<ExtentToArrange> elements, int numberRows, boolean varyNumberImagesPerRow)
            throws OperationFailedException {
        if (varyNumberImagesPerRow) {
            return LinearPartition.partition(elements, ExtentToArrange::getAspectRatio, numberRows);
        } else {
            return partitionIntoFixedChunks(elements, numberRows);
        }
    }

    /**
     * Partition {@code elements} into fixed chunks of particular fixed size (apart from the last
     * row).
     */
    private static <T> List<List<T>> partitionIntoFixedChunks(List<T> elements, int numberRows) {
        List<List<T>> out = new ArrayList<>(numberRows);

        // Calculate the number of images per row
        int imagesPerRow = (int) Math.ceil(((double) elements.size()) / numberRows);
        int count = 0;
        List<T> current = new LinkedList<>();
        for (T element : elements) {
            current.add(element);
            count++;
            if (count == imagesPerRow) {
                out.add(current);
                current = new LinkedList<>();
                count = 0;
            }
        }
        if (count != 0) {
            out.add(current);
        }
        return out;
    }
}
