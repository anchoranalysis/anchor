package org.anchoranalysis.image.bean.spatial.arrange.align;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Helps test a {@link BoxAligner}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class BoxAlignerTester {

    /** The smaller bounding-box passed to {@link BoxAligner#align}. */
    public static final BoundingBox SMALLER =
            new BoundingBox(new Point3i(20, 30, 0), new Extent(10, 20, 30));

    /** The larger bounding-box passed to {@link BoxAligner#align}. */
    public static final BoundingBox LARGER =
            new BoundingBox(new Point3i(5, 10, 15), new Extent(100, 200, 30));

    /**
     * Calls {@link BoxAligner#align} with the smaller and larger bounding box, and checks the
     * expected result.
     *
     * @param aligner the {@link BoxAligner} to use.
     * @param expectedCorner the expected corner on the returned {@link BoundingBox}.
     * @param expectedExtent the expected extent on the returned {@link BoundingBox}.
     * @throws OperationFailedException if thrown by {@link BoxAligner}.
     */
    public static void doTest(
            BoxAligner aligner, ReadableTuple3i expectedCorner, Extent expectedExtent)
            throws OperationFailedException {
        BoundingBox box = aligner.align(SMALLER, LARGER);
        assertEquals(expectedCorner, box.cornerMin(), "cornerMin");
        assertEquals(expectedExtent, box.extent(), "extent");
    }
}
