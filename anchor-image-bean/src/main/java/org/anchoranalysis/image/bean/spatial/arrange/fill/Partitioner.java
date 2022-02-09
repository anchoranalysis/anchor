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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.ToDoubleFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;

/**
 * Partitions a list of {@link ExtentToArrange}s into batches.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class Partitioner {

    /**
     * Partitions {@code elements} into lists, that each represent a particular batch.
     *
     * <p>A batch may be a row or column.
     *
     * @param elements the elements to partition.
     * @param numberBatches how many batches to partition into.
     * @param varyNumberImagesPerBatch if the number of elements in each batch may vary (i.e. it
     *     must not be uniform) so as to make better use of space.
     * @param batchIsRow when true, each batch represents a row in the table. when false, each batch
     *     represents a column in the table.
     * @return a list of batches, where each batch is a subset of elements after partitioning. Each
     *     element in {@code elements} will appear exactly once in total across these nested lists.
     * @throws OperationFailedException if there are too few items in {@code elements} to be split
     *     into partitions, when {@code varyNumberImagesPerBatch==true}.
     */
    public static List<List<ExtentToArrange>> partitionExtents(
            List<ExtentToArrange> elements,
            int numberBatches,
            boolean varyNumberImagesPerBatch,
            boolean batchIsRow)
            throws OperationFailedException {
        if (varyNumberImagesPerBatch) {
            ToDoubleFunction<ExtentToArrange> extractCost =
                    batchIsRow
                            ? ExtentToArrange::getAspectRatio
                            : ExtentToArrange::aspectRatioInverted;
            return LinearPartition.partition(elements, extractCost, numberBatches);
        } else {
            return partitionIntoBatches(elements, numberBatches);
        }
    }

    /**
     * Partition {@code elements} into fixed-sized batches (apart from the last batch that may have
     * fewer).
     */
    private static <T> List<List<T>> partitionIntoBatches(List<T> elements, int numberBatches) {
        List<List<T>> out = new ArrayList<>(numberBatches);

        // Calculate the number of images per batch
        int imagesPerBatch = (int) Math.ceil(((double) elements.size()) / numberBatches);
        int count = 0;
        List<T> current = new LinkedList<>();
        for (T element : elements) {
            current.add(element);
            count++;
            if (count == imagesPerBatch) {
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
