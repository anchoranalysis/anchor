package org.anchoranalysis.anchor.mpp.mark;

/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.util.Set;

import org.anchoranalysis.core.geometry.Point3d;

import com.google.common.base.Function;

class PointInSetQuery {

	public static boolean anyCrnrInSet(Point3d pnt, Set<Point3d> set) {
		
		// We test if any combination of the ceil, floor can be found in the set
		//  in 2 dimensions.   i.e. the four corners of a pixel around the point
		
		return pntInSet(pnt, set, null, null, null )
		|| pntInSet(pnt, set, Math::floor, Math::ceil, null )
		|| pntInSet(pnt, set, Math::ceil, Math::floor, null )
		|| pntInSet(pnt, set, Math::ceil, Math::ceil, null )
		|| pntInSet(pnt, set, Math::floor, Math::floor, null );
	}
	
	private static boolean pntInSet(
		Point3d pnt,
		Set<Point3d> set,
		Function<Double,Double> funcX,
		Function<Double,Double> funcY,
		Function<Double,Double> funcZ
	) {
		Point3d pntNew = new Point3d(
			applyFuncIfNonNull(pnt.getX(), funcX),
			applyFuncIfNonNull(pnt.getY(), funcY),
			applyFuncIfNonNull(pnt.getZ(), funcZ)
		);
		return pntInSet(pntNew, set);
	}
	
	private static double applyFuncIfNonNull( double in, Function<Double,Double> func ) {
		if (func!=null) {
			return func.apply(in);
		} else {
			return in;
		}
	}
	
	private static boolean pntInSet(Point3d pnt, Set<Point3d> set) {
		return set.contains(pnt);
	}
}
