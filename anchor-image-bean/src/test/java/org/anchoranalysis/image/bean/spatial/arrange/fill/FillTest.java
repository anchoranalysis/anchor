package org.anchoranalysis.image.bean.spatial.arrange.fill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
import org.anchoranalysis.bean.shared.color.scheme.HSB;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.resizer.Linear;
import org.anchoranalysis.image.voxel.resizer.VoxelsResizer;
import org.anchoranalysis.spatial.box.Extent;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Fill}.
 *
 * @author Owen Feehan
 */
class FillTest {

    /** How many {@link Extent}s to use as inputs in the test. */
    private static final int NUMBER_EXTENTS = 15;

    /** The number of rows in the combined image. */
    private static final int COMBINED_NUMBER_ROWS = 3;

    /** What ratio of the original aspect-ratio to allow as a tolerance. */
    private static final double TOLERANCE_ASPECT_RATIO = 0.05;

    /** For resizing images with interpolation. */
    private static final VoxelsResizer RESIZER = new Linear();

    private static final ColorList COLORS = new HSB().createList(NUMBER_EXTENTS);

    @Test
    void testFill() throws ArrangeStackException, OperationFailedException {

        // Create extents incrementally
        List<RGBStack> stacks = IncrementallyLargerStacksFixture.createStacks(COLORS);
        RGBStack combined = new Fill(COMBINED_NUMBER_ROWS).combine(stacks, RESIZER);

        // Check that each color appears in an expected way.
        assertColorChecks(combined, index -> stacks.get(index).extent());
    }

    /** Performs several checks on each extracted {@link ObjectMask} representing each color. */
    private static void assertColorChecks(RGBStack combined, IntFunction<Extent> originalSize)
            throws OperationFailedException {

        // Check that no voxel has black in any dimension
        assertTrue(!combined.objectWithColor(new RGBColor(Color.BLACK)).isPresent());

        for (int i = 0; i < COLORS.size(); i++) {
            // Extract a color and check its aspect ratio
            Optional<ObjectMask> color = combined.objectWithColor(COLORS.get(i));

            assertTrue(color.isPresent(), "the color exists");

            // Check that no OFF voxel exists, as all input images were rectangularly-filled with a
            // color
            assertTrue(!color.get().voxelsOff().anyExists(), "no off voxel exists");

            assertSimilarAspectRatios(originalSize.apply(i), color.get().extent());
        }
    }

    /**
     * Asserts the aspect-ratio is similar between the before and after.
     *
     * <p>A tolerance is allowed, relative to the size of the {@code before} aspect-ratio.
     */
    private static void assertSimilarAspectRatios(Extent before, Extent after) {
        double beforeAspectRatio = before.aspectRatioXY();
        double afterAspectRatio = after.aspectRatioXY();
        double tolerance = beforeAspectRatio * TOLERANCE_ASPECT_RATIO;
        assertEquals(
                beforeAspectRatio,
                afterAspectRatio,
                tolerance,
                "input and output aspect ratio is similar");
    }
}