package org.anchoranalysis.image.scale;

/*
 * #%L
 * anchor-image-bean
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


/**
 * What to scale x and y dimensions by 
 * 
 * <p>This class is IMMUTABLE.</p>
 * 
 * @author Owen Feehan
 *
 */
public final class ScaleFactor {
	
	private final double x;
	private final double y;
	
	public ScaleFactor(double factor) {
		this(factor, factor);
	}
	
	public ScaleFactor(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	
	public ScaleFactor invert() {
		return new ScaleFactor(1/x,1/y);
	}
	
	public boolean hasIdenticalXY() {
		return Math.abs(x-y) < 1e-3;
	}

	@Override
	public String toString() {
		return String.format("x=%f\ty=%f\t\tx^-1=%f\ty^-1=%f",x,y,1/x,1/y);
	}
	
}