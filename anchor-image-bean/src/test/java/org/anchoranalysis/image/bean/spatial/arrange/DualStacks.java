package org.anchoranalysis.image.bean.spatial.arrange;

import java.util.Arrays;
import java.util.List;

import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/** 
 * Two {@link RGBStack} of different sizes, with specific respective colors.
 */
class DualStacks {
	
	/** The size of the <i>big</i> stack/ */
	private static final Extent SIZE_BIG = new Extent(60,50,30);
	
	/** The size of the <i>small</i> stack/ */
	private static final Extent SIZE_SMALL = new Extent(20,15,10);
			
	/** The <i>minimum</i> point (inclusive) in each dimension for the <i>big<i> stack. */
	public static final Point3i BIG_CORNER_MIN = new Point3i(0,0,0);
	
	/** The <i>maximum</i> point (inclusive) in each dimension for the <i>big<i> stack. */
	public static final Point3i BIG_CORNER_MAX = SIZE_BIG.createMinusOne();
	
	/**
	 * Create a list containing the two {@link RGBStack}s.
	 * 
	 * @param colorBig the color of all voxels in the <i>big</i> stack.
	 * @param colorSmall the color of all voxels in the <i>small</i> stack.
	 * @return a newly created list, containing the two respective (newly created) {@link RGBStack}s.
	 */
	public static List<RGBStack> asList(RGBColor colorBig, RGBColor colorSmall) {
		RGBStack big = new RGBStack(SIZE_BIG, colorBig);
		RGBStack small = new RGBStack(SIZE_SMALL,colorSmall);
		return Arrays.asList(big,small);
	}
}