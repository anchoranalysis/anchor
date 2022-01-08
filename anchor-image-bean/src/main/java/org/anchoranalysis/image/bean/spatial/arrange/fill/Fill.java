package org.anchoranalysis.image.bean.spatial.arrange.fill;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Value;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionalIterate;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
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
 * implementation by Jesse Crocker</a>, which is in turn apparently inspired from <a
 * href="https://www.crispymtn.com/stories/the-algorithm-for-a-perfectly-balanced-photo-gallery
 * collage.py">now missing site</a> (see <a
 * href="https://web.archive.org/web/20131113185038/http://www.crispymtn.com/stories/the-algorithm-for-a-perfectly-balanced-photo-gallery">archive</a>).
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@AllArgsConstructor
public class Fill extends StackArranger {

    /**
     * A particular stack to be arranged, with calculated aspect-ratio, and its unique index
     * position.
     */
    @Value
    private static class Element {

        /** Index position. */
        private final int index;

        /** The size of the stack. */
        private final Extent extent;

        /** The aspect-ratio: width of {@code extent} divided by height. */
        private final double aspectRatio;

        public Element(int index, Extent extent) {
            this.index = index;
            this.extent = extent;
            this.aspectRatio = extent.aspectRatioXY();
        }
    }

    // START BEAN PROPERTIES
    /**
     * The number of rows in the montage. If fewer {@link Extent}s are passed than this, the rows
     * may be fewer.
     */
    @BeanField @Getter @Setter private int rows = 1;

    @BeanField @Getter @Setter private int width = 4096;
    // END BEAN PROPERTIES

    /**
     * Create for a certain number of rows.
     *
     * @param rows the number of rows.
     */
    public Fill(int rows) {
        this.rows = rows;
    }

    @Override
    public StackArrangement arrangeStacks(Iterator<Extent> extents) throws ArrangeStackException {
        List<Element> elements = createElements(extents);

        // In case there are fewer elements than rows, we change the number of rows
        int rowsSelected = Math.min(rows, elements.size());

        try {
            List<List<Element>> partitions =
                    LinearPartition.partition(elements, Element::getAspectRatio, rowsSelected);
            double resultHeight = 0.0;
            for (List<Element> row : partitions) {
                double rowWidth = sumAspectRatios(row);
                Element element = row.get(0);
                double scaleRatio = extractScaleFactor(element, rowWidth);
                resultHeight += (element.getExtent().y() * scaleRatio);
            }

            Extent resultSize = new Extent(width, (int) resultHeight, 1);

            // Create boxes populated with null for each element
            List<BoundingBox> boxes = new ArrayList<>(elements.size());
            FunctionalIterate.repeat(elements.size(), () -> boxes.add(null));

            StackArrangement arrangement = new StackArrangement(resultSize, boxes);

            int yPosition = 0;
            for (List<Element> row : partitions) {
                double rowWidth = sumAspectRatios(row);
                int xPosition = 0;
                int rowHeight = 0;

                for (Element element : row) {
                    double scaleRatio = extractScaleFactor(element, rowWidth);

                    Extent sizeNew = element.getExtent().scaleXYBy(scaleRatio, false);

                    Point3i cornerMin = new Point3i(xPosition, yPosition, 0);

                    BoundingBox box = BoundingBox.createReuse(cornerMin, sizeNew);
                    box = box.clampTo(resultSize);

                    boxes.set(element.getIndex(), box);

                    xPosition += sizeNew.x();
                    rowHeight = sizeNew.y();
                }
                yPosition += rowHeight;
            }

            for (BoundingBox box : arrangement.boxes()) {
                assert (arrangement.extent().contains(box));
            }

            return arrangement;

        } catch (OperationFailedException e) {
            throw new ArrangeStackException("An error occurred partitioning the elements", e);
        }
    }

    private double extractScaleFactor(Element element, double rowWidth) {
        double widthPercent = element.getAspectRatio() / rowWidth;
        double imageWidth = widthPercent * width;
        return imageWidth / element.getExtent().x();
    }

    private double sumAspectRatios(List<Element> elements) {
        return elements.stream().mapToDouble(element -> element.getAspectRatio()).sum();
    }

    private static List<Element> createElements(Iterator<Extent> extents) {
        int index = 0;
        List<Element> elements = new ArrayList<>();
        while (extents.hasNext()) {
            elements.add(new Element(index++, extents.next()));
        }
        return elements;
    }
}
