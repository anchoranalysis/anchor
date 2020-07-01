package org.anchoranalysis.image.extent;

/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.geometry.ReadableTuple3i;

// Helper classes for calculating the union/intersection along each axis
class ExtentBoundsComparer {

	private int min;
	private int extent;
	
	public static ExtentBoundsComparer createMax(
		ReadableTuple3i min1,
		ReadableTuple3i min2,
		ReadableTuple3i max1,
		ReadableTuple3i max2,
		Function<ReadableTuple3i,Integer> extract
	) {
		return calc(
			extract.apply(min1),
			extract.apply(min2),
			extract.apply(max1),
			extract.apply(max2),
			Math::min,
			Math::max
		).orElseThrow(AnchorImpossibleSituationException::new);
	}
	
	public static Optional<ExtentBoundsComparer> createMin(
		ReadableTuple3i min1,
		ReadableTuple3i min2,
		ReadableTuple3i max1,
		ReadableTuple3i max2,
		Function<ReadableTuple3i,Integer> extract
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

	private ExtentBoundsComparer(int min, int extent) {
		super();
		this.min = min;
		this.extent = extent;
	}
	
	public int getMin() {
		return min;
	}

	public int getExtnt() {
		return extent;
	}

}
