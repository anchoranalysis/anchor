package org.anchoranalysis.anchor.mpp.mark.conic;

/*
 * #%L
 * anchor-mpp
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


import org.anchoranalysis.anchor.mpp.bean.bound.Bound;
import org.anchoranalysis.anchor.mpp.mark.Mark;


/**
 * A sphere
 * 
 * @author Owen Feehan
 *
 */
public class MarkSphere extends MarkSingleRadius {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -3526056946146656810L;
    
    // Sphere with default properties
    public MarkSphere() {
    }
    
    // Constructor
    public MarkSphere( Bound boundRadius ) {
    	super(boundRadius);
    }
    
    // Copy Constructor - we do not copy scene
    public MarkSphere( MarkSphere src ) {
    	super( src );
    }
	
	@Override
	public String getName() {
		return "sphere";
	}
    
    @Override
	public double volume( int regionID ) {
    	double radiusCubed = Math.pow(
    		radiusForRegion(regionID),
    		3.0
    	);
    	return (4 * Math.PI * radiusCubed) /3;
    }
    
    @Override
	public String toString() {
    	return String.format("%s %s pos=%s %s", "Sphr", strId(), strPos(), strMarks() );
    }
	
	// The duplicate operation for the marks (we avoid clone() in case its confusing, we might not always shallow copy)
	@Override
	public Mark duplicate() {
		return new MarkSphere( this );
	}

	@Override
	public int numDims() {
		return 3;
	}
}
