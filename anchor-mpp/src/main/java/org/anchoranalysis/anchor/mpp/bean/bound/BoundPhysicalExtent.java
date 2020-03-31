package org.anchoranalysis.anchor.mpp.bean.bound;

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

import org.anchoranalysis.image.extent.ImageRes;

//
//  An upper and lower bound in degrees which is converted
//   to radians when resolved
// 
public class BoundPhysicalExtent extends BoundMinMax {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5440824428055546445L;
	//private static Log log = LogFactory.getLog(BoundPhysicalExtent.class);
	
	public BoundPhysicalExtent() {
		super();
	}
	
	public BoundPhysicalExtent( double min, double max ) {
		super(min,max);
	}
	
	public BoundPhysicalExtent( BoundPhysicalExtent src ) {
		super(src);
	}
	
	@Override
	public double getMinRslvd( ImageRes sr, boolean do3D ) {
		double rslvd = getMin() / sr.min(do3D);
		//log.debug( String.format("min-rslvd %e to %f", getMin(), rslvd));
		return rslvd;
	}
	
	@Override
	public double getMaxRslvd( ImageRes sr, boolean do3D ) {
		double rslvd = getMax() / sr.min(do3D); 
		//log.debug( String.format("max-rslvd %e to %f", getMax(), rslvd));
		return rslvd;
	}
	
	@Override
	public Bound duplicate() {
		return new BoundPhysicalExtent( this );
	}
}