package org.anchoranalysis.image.bean.spatial.arrange;

import java.awt.Color;

import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.core.stack.RGBStack;

/**
 * Two {@link RGBStack} of different sizes, the bigger colored entirely in cyan, and the smaller in magenta.
 * 
 * @author Owen Feehan
 *
 */
class ColoredDualStacks {

	public static final RGBColor CYAN = new RGBColor(Color.CYAN);
	public static final RGBColor MAGENTA = new RGBColor(Color.MAGENTA);
		
	/** 
	 * Combines a big cyan-colored image with a smaller magenta-colored image.
	 * 
	 * @param arranger how to arrange the two stacks when combined.
	 * @param flattenSmall when true, the smaller stack is flattend across the z-dimension.
	 * @return a newly created {@link RGBStack} representing the combination.
	 * @throws ArrangeStackException if thrown by {@link DualStacks#asList}.
	 */
	public static RGBStack combine(StackArranger arranger, boolean flattenSmall) throws ArrangeStackException {
		return arranger.combine( DualStacks.asList(CYAN, MAGENTA, flattenSmall) );
	}
}
