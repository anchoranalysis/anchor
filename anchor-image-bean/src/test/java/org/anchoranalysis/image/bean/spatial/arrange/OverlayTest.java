package org.anchoranalysis.image.bean.spatial.arrange;

import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.spatial.point.Point3i;

import static org.anchoranalysis.image.bean.spatial.arrange.ColoredDualStacks.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Overlay}.
 * 
 * @author Owen Feehan
 *
 */
class OverlayTest {
		
	/** Tests aligned towards the <b>minimum</b> on every axis. */
	@Test
	void testMin() throws ArrangeStackException {
		doTest(new Overlay("left", "top", "bottom"), MAGENTA, CYAN, CYAN, false);
	}
	
	/** Tests aligned towards the <b>center</b> on every axis. */
	@Test
	void testCenter() throws ArrangeStackException {
		doTest(new Overlay("center", "center", "center"), CYAN, MAGENTA, CYAN, false);
	}
	
	/** Tests aligned towards the <b>max</b> on every axis. */
	@Test
	void testMax() throws ArrangeStackException {
		doTest(new Overlay("right", "bottom", "top"), CYAN, CYAN, MAGENTA, false);
	}
	
	/** Tests with an overlay with a single z-slice. */
	@Test
	void testRepeatSingleSlice() throws ArrangeStackException {
		doTest(new Overlay("left", "top", "repeat"), MAGENTA, MAGENTA, CYAN, true);
	}

	/** Tests with an overlay with multiple z-slices. */
	@Test
	void testRepeatMultipleSlices() throws ArrangeStackException {
		assertThrows(ArrangeStackException.class, () -> 
			ColoredDualStacks.combine(
				new Overlay("right", "bottom", "repeat"),
				false
			)
		);
	}
	
	/** Combines the two stacks, and asserts the expected color of the minimum-corner and the maximum-corner of the big stack. */
	private static void doTest(StackArranger arranger, RGBColor colorExpectedMin, RGBColor colorExpectedMidpoint, RGBColor colorExpectedMax, boolean flattenSmall) throws ArrangeStackException {
		RGBStack combined = ColoredDualStacks.combine(arranger, flattenSmall);
		assertVoxelColor("minCorner", combined, DualStacks.BIG_CORNER_MIN, colorExpectedMin);
		assertVoxelColor("midpoint", combined, DualStacks.BIG_MIDPOINT, colorExpectedMidpoint);
		assertVoxelColor("maxCorner", combined, DualStacks.BIG_CORNER_MAX, colorExpectedMax);
	}
	
	/** Asserts a particular color exists at a voxel. */
	private static void assertVoxelColor(String message, RGBStack stack, Point3i point, RGBColor expectedColor) {
		assertEquals(expectedColor, stack.colorAtVoxel(point), message);
	}
}
