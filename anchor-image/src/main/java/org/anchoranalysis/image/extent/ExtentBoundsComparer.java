package org.anchoranalysis.image.extent;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.geometry.Point3i;

// Helper classes for calculating the union/intersection along each axis
class ExtentBoundsComparer {

	private int min;
	private int extnt;
	
	public static ExtentBoundsComparer createMax(
		Point3i min1,
		Point3i min2,
		Point3i max1,
		Point3i max2,
		Function<Point3i,Integer> extract
	) {
		return calc(
			extract.apply(min1),
			extract.apply(min2),
			extract.apply(max1),
			extract.apply(max2),
			Math::min,
			Math::max
		).orElseThrow( ()->
			new AnchorFriendlyRuntimeException("ExtentIntersector encountered an invalid order. This should never occur.")
		);
	}
	
	public static Optional<ExtentBoundsComparer> createMin(
		Point3i min1,
		Point3i min2,
		Point3i max1,
		Point3i max2,
		Function<Point3i,Integer> extract
	) {
		return calc(
			extract.apply(min1),
			extract.apply(min2),
			extract.apply(max1),
			extract.apply(max2),
			Math::max,
			Math::min
		);
	}
	
	private static Optional<ExtentBoundsComparer> calc(
		int min1,
		int min2,
		int max1,
		int max2,
		BiFunction<Integer,Integer,Integer> minOp,
		BiFunction<Integer,Integer,Integer> maxOp
	) {
		int minNew = minOp.apply(min1, min2);
		int maxNew = maxOp.apply(max1, max2);
		if (minNew <= maxNew) {
			return Optional.of(
				new ExtentBoundsComparer(
					minNew,
					maxNew - minNew + 1
				)
			);
		} else {
			return Optional.empty();
		}
	}

	private ExtentBoundsComparer(int min, int extnt) {
		super();
		this.min = min;
		this.extnt = extnt;
	}
	
	public int getMin() {
		return min;
	}

	public int getExtnt() {
		return extnt;
	}

}