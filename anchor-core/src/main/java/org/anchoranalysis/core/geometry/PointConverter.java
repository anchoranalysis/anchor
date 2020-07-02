package org.anchoranalysis.core.geometry;

/*
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PointConverter {

	private PointConverter() {}
	
	// START singular points
	
	public static Point2i intFromDouble(Point2d point) {
		return new Point2i( (int) point.getX(), (int) point.getY() );
	}
	
	public static Point3i intFromDouble(Point3d point) {
		return new Point3i(
			(int) point.getX(),
			(int) point.getY(),
			(int) point.getZ()
		);
	}
	
	public static Point3i intFromDoubleCeil(Point3d point) {
		return new Point3i(
			(int) Math.ceil(point.getX()),
			(int) Math.ceil(point.getY()),
			(int) Math.ceil(point.getZ())
		);
	}
	
	public static Point3i convertTo3d(Point2i point) {
		return new Point3i(point.getX(), point.getY(), 0);
	}
	
	public static Point2d doubleFromFloat(Point2f point) {
		return new Point2d( point.getX(), point.getY() );
	}
	
	public static Point3d doubleFromFloat(Point3f point) {
		return new Point3d( point.getX(), point.getY(),	point.getZ() );
	}
	
	public static Point3d doubleFromInt(Point2i point) {
		return new Point3d( (double) point.getX(), (double) point.getY(), 0);
	}
	
	public static Point3d doubleFromInt(ReadableTuple3i point) {
		return new Point3d( (double) point.getX(), (double) point.getY(), (double) point.getZ() );
	}
	
	public static Point3f floatFromInt(Point2i point) {
		return new Point3f( (float) point.getX(), (float) point.getY(), 0);
	}
	
	public static Point3f floatFromDouble(Point3d point) {
		return new Point3f( (float) point.getX(), (float) point.getY(), (float) point.getZ() );
	}

	
	public static Point3f floatFromInt(ReadableTuple3i point) {
		return new Point3f( (float) point.getX(), (float) point.getY(), (float) point.getZ());
	}
	
	public static Point3i intFromFloat(Point3f point, boolean round) {
		if (round) {
			return new Point3i(
				roundInt(point.getX()),
				roundInt(point.getY()),
				roundInt(point.getZ())
			);
		} else {
			return new Point3i(
				ceilInt(point.getX()),
				ceilInt(point.getY()),
				ceilInt(point.getZ())
			);
			
		}
	}

	// END singular points
	
	// START lists of points
	
	public static List<Point3f> convert3i_3f( List<Point3i> points ) {
		return convert( points, PointConverter::floatFromInt );
	}
	
	public static List<Point3f> convert3d_3f( List<Point3d> points ) {
		return convert( points, PointConverter::floatFromDouble );
	}
	
	public static List<Point3f> convert2i_3f( List<Point2i> points ) {
		return convert( points, PointConverter::floatFromInt );
	}
	
	public static List<Point3d> convert2i_3d( List<Point2i> points ) {
		return convert( points, PointConverter::doubleFromInt );
	}
	
	public static List<Point3d> convert3f_3d( List<Point3f> points ) {
		return convert( points, PointConverter::doubleFromFloat );
	}
	
	public static List<Point3i> convert3i( List<Point3f> points, boolean round ) {
		return convert(
			points,
			pnt -> intFromFloat(pnt, round) 
		);
	}
		
	public static List<Point3i> convert3i( List<Point3d> points ) {
		return convert( points, PointConverter::intFromDouble );
	}
	
	// END lists of points
		
	private static <S,T> List<S> convert( List<T> points, Function<T,S> mapFunction ) {
		return points
				.stream()
				.map(mapFunction)
				.collect( Collectors.toList() );
	}
			
	private static int roundInt( double value ) {
		return (int) Math.round(value);
	}
	
	private static int ceilInt( double value ) {
		return (int) Math.ceil(value);
	}
}
