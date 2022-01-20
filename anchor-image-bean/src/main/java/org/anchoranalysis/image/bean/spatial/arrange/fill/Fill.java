package org.anchoranalysis.image.bean.spatial.arrange.fill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.time.OperationContext;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.BoundingBoxEnclosed;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.StackArrangement;
import org.anchoranalysis.image.bean.spatial.arrange.StackArranger;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Montages {@link Extent}s together to completely fill the available space, while preserving aspect
 * ratio.
 *
 * <p>Inspired by this <a href="https://gist.github.com/JesseCrocker/cfd05006335c2c828a2b">Python
 * implementation by Jesse Crocker</a>, which is in turn apparently inspired originally from {@code
 * https://www.crispymtn.com/stories/the-algorithm-for-a-perfectly-balanced-photo-gallery
 * collage.py}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@AllArgsConstructor
public class Fill extends StackArranger {

    // START BEAN PROPERTIES
    /**
     * The number of rows to use the montage, when sufficient images are available.
     *
     * <p>If fewer {@link Extent}s are passed, then a row is created for each {@link Extent}, so
     * much as possible.
     */
    @BeanField @Getter @Setter private int numberRows = 1;

    /**
     * When true the number of {@link Extent}s per row is allowed vary, to sensibly fill space.
     *
     * <p>When false, the total number of {@link Extent}s per row is kept uniform, apart from the
     * final occupied row (if there are insufficient images to fully populate it).
     *
     * <p>In both cases, the sizes of the images are allowed to vary, to maximally fill the
     * available space.
     */
    @BeanField @Getter @Setter private boolean varyNumberImagesPerRow = true;
    // END BEAN PROPERTIES

    /**
     * Create with a particular number of rows.
     *
     * @param rows the number of rows to use the montage, when sufficient images are available.
     */
    public Fill(int rows) {
        this.numberRows = rows;
    }

    @Override
    public StackArrangement arrangeStacks(Iterator<Extent> extents, OperationContext context)
            throws ArrangeStackException {
        List<ExtentToArrange> elements = createElements(extents);

        // In case there are fewer elements than rows, we change the number of rows
        int numberRowsMin = Math.min(numberRows, elements.size());

        try {
            // Partition the images, so that each row has a roughly similar sum of aspect-ratios
            List<List<ExtentToArrange>> partitions =
                    context.getExecutionTimeRecorder()
                            .recordExecutionTime(
                                    "Partition extents",
                                    () -> partitionExtents(elements, numberRowsMin));

            Extent combinedSize =
                    FitCombinedScaler.scaleImagesToCombine(partitions, !varyNumberImagesPerRow);

            // Create a bounding box for each element, using the ID to determine the position in the
            // array.
            BoundingBox[] boxes = derivingBoundingBoxes(elements.size(), partitions, combinedSize);

            List<BoundingBoxEnclosed> boxesEnclosed =
                    FunctionalList.mapToList(Arrays.stream(boxes), BoundingBoxEnclosed::new);

            return new StackArrangement(combinedSize, boxesEnclosed);

        } catch (OperationFailedException e) {
            throw new ArrangeStackException("An error occurred partitioning the elements", e);
        }
    }

    /** Partitions {@code elements} into smaller lists, each representing a particular row. */
    private List<List<ExtentToArrange>> partitionExtents(
            List<ExtentToArrange> elements, int numberRows) throws OperationFailedException {
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

    /** Creates a {@link ExtentToArrange} for each respective {@link Extent}. */
    private static List<ExtentToArrange> createElements(Iterator<Extent> extents) {
        int index = 0;
        List<ExtentToArrange> elements = new LinkedList<>();
        while (extents.hasNext()) {
            elements.add(new ExtentToArrange(index++, extents.next()));
        }
        return elements;
    }

    /**
     * Derive a {@link BoundingBox} for each {@link Extent}, corresponding to its location and size
     * in the combined size.
     *
     * @throws OperationFailedException if any bounding box lies outside the image.
     */
    private static BoundingBox[] derivingBoundingBoxes(
            int numberElements, List<List<ExtentToArrange>> partitions, Extent combinedSize)
            throws OperationFailedException {
        BoundingBox[] boxes = new BoundingBox[numberElements];

        Point3i cornerMin = new Point3i();
        for (List<ExtentToArrange> row : partitions) {
            cornerMin.setX(0);
            Extent extent = null;
            assert (!row.isEmpty());

            for (ExtentToArrange element : row) {
                extent = element.getExtent();

                if (!combinedSize.contains(cornerMin)) {
                    throw new OperationFailedException(
                            String.format(
                                    "Element %d with corner %s is not located inside the montaged image of size %s",
                                    element.getIndex(), cornerMin, combinedSize));
                }

                BoundingBox box =
                        BoundingBox.createDuplicate(cornerMin, extent).clampTo(combinedSize);
                boxes[element.getIndex()] = box;

                cornerMin.incrementX(extent.x());
            }
            cornerMin.incrementY(extent.y()); // NOSONAR
        }

        return boxes;
    }
}
