package org.anchoranalysis.image.bean.spatial.arrange;

import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.spatial.point.Point3i;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Overlay}.
 * 
 * @author Owen Feehan
 *
 */
class OverlayTest {

	private static final RGBColor CYAN = new RGBColor(Color.CYAN);
	private static final RGBColor MAGENTA = new RGBColor(Color.MAGENTA);
		
	@Test
	void testMinAlign() throws ArrangeStackException {
		doTest(new Overlay("left", "top", "bottom"), MAGENTA, CYAN);
	}
	
	@Test
	void testMaxAlign() throws ArrangeStackException {
		doTest(new Overlay("right", "bottom", "top"), CYAN, MAGENTA);
	}
	
	/** Combines the two stacks, and asserts the expected color of the minimum-corner and the maximum-corner of the big stack. */
	private static void doTest(StackArranger arranger, RGBColor colorExpectedMin, RGBColor colorExpectedMax) throws ArrangeStackException {
		RGBStack combined = arranger.combine( DualStacks.asList(CYAN, MAGENTA) );
		assertVoxelColor(combined, DualStacks.BIG_CORNER_MIN, colorExpectedMin);
		assertVoxelColor(combined, DualStacks.BIG_CORNER_MAX, colorExpectedMax);
	}
	
	/** Asserts a particular color exists at a voxel. */
	private static void assertVoxelColor(RGBStack stack, Point3i point, RGBColor expectedColor) {
		assertEquals(expectedColor, stack.colorAtVoxel(point));
	}
}
