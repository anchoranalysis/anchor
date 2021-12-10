package org.anchoranalysis.spatial.rtree;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.BoundingBoxFactory;

/**
 * Creates lists of bounding-boxes, constructed from identical bounding-boxes that incrementally
 * shift their position.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class IncrementingBoxFixture {

    /** The width, height, and depth of each box that is created. */
    private static final int BOX_SIZE = 20;

    /**
     * This is added to the minimum corner of the previous box, to give the next box in a sequence.
     */
    private static final int POSITION_SHIFT = 3;

    /**
     * Creates a list of boxes, partitioned into two disjoint sets.
     *
     * @param numberFirst the number of boxes in the <i>first</i> set.
     * @param numberSecond the number of boxes in the <i>second</i> set.
     * @return a newly created {@link List} containing {@code numberBoxes}, incrementally shifted,
     *     and in two disjoint sets.
     */
    public static List<BoundingBox> createDisjointIncrementing(int numberFirst, int numberSecond) {
        List<BoundingBox> boxes = createIncrementingBoxes(numberFirst, 0);

        // Make sure the second set starts after sufficient space from the last set
        int startSecond = ((numberFirst + 2) * BOX_SIZE);
        boxes.addAll(createIncrementingBoxes(numberSecond, startSecond));
        return boxes;
    }

    /**
     * Creates a list of identically-sized boxes, incrementing shifting their position.
     *
     * @param numberBoxes the number of boxes to create.
     * @param initialPosition the corner position for the first box.
     * @return a newly created {@link List} containing {@code numberBoxes}, incrementally shifted.
     */
    public static List<BoundingBox> createIncrementingBoxes(int numberBoxes, int initialPosition) {
        List<BoundingBox> out = new ArrayList<BoundingBox>();
        int position = initialPosition;
        for (int i = 0; i < numberBoxes; i++) {
            out.add(BoundingBoxFactory.uniform3D(position, BOX_SIZE));
            position += POSITION_SHIFT;
        }
        return out;
    }
}
