package org.anchoranalysis.image.scale;

import org.anchoranalysis.image.extent.Extent;

/*-
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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


public class ScaleFactorUtilities {
	
	/**
	 * Calculates a scaling factor so as to scale sdSource to sdTarget
	 * 
	 * <p>i.e. the scale-factor is target/source for each XY dimension</p>
	 * 
	 * @param source source extent
	 * @param target target extent
	 * @return the scaling-factor to scale the source to be the same size as the target
	 */
	public static ScaleFactor calcRelativeScale( Extent source, Extent target ) {
		return new ScaleFactor(
			divideAsDouble( target.getX(), source.getX() ),
			divideAsDouble( target.getY(), source.getY() )
		);
	}
		
	/**
	 * Multiplies a double by an integer, returning it as an integer
	 * 
	 * @param first the double
	 * @param second the int
	 * @return the multiple floored to an integer
	 */
	public static int multiplyAsInt( double first, int second ) {
		return (int) first * second; 
	}
	
	
	/**
	 * Divides two integers as a double
	 * @param numerator to divide by
	 * @param denominator  divider
	 * @return floating-point result of division
	 */
	private static double divideAsDouble( int numerator, int denominator ) {
		return ((double) numerator) / denominator;
	}
}
