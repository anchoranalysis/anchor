package org.anchoranalysis.image.extent;

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


import static org.junit.Assert.assertTrue;

import org.anchoranalysis.core.geometry.Point3i;
import org.junit.Test;

public class BoundingBoxTest {

	@Test 
	public void testIntersect1() {
		//fail("Not yet implemented");
		
		BoundingBox obj1 = new BoundingBox( new Point3i(154,58,3), new Extent(12,30,16)); 
		BoundingBox obj2 = new BoundingBox( new Point3i(46, 62, 5), new Extent(26, 24, 17));
		
		assertTrue( !obj1.intersection().existsWith(obj2) );
		
	}
	
	@Test 
	public void testIntersect2() {
		//fail("Not yet implemented");
		
		BoundingBox obj1 = new BoundingBox( new Point3i(0,0,0), new Extent(1024,1024,1)); 
		BoundingBox obj2 = new BoundingBox( new Point3i(433, 95, 1), new Extent(1, 1, 1));
		
		assertTrue( !obj1.intersection().existsWith(obj2) );
		
	}
}

