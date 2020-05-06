package org.anchoranalysis.core.geometry;

/*
 * #%L
 * anchor-core
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


import static org.junit.Assert.*;

import org.junit.Test;

public class PointTest {

	@Test
	public void testEquals() {
		
		Point2d pnt2d = new Point2d(-3.1,4.2);
		assertTrue( pnt2d.equals( new Point2d(-3.1,4.2) ) );
		
		Point2f pnt2f = new Point2f(-3.1f,4.2f);
		assertTrue( pnt2f.equals( new Point2f(-3.1f,4.2f) ) );
		
		// At present, different types are not allowed be equal
		assertTrue( pnt2f.equals( pnt2d.toFloat() ) );
		
		Point2i pnt2i = new Point2i(-3,4);
		assertTrue( pnt2i.equals( new Point2i(-3,4) ) );
		
		
		
		Point3d pnt3d = new Point3d(-3.1,4.2,6.3);
		assertTrue( pnt3d.equals( new Point3d(-3.1,4.2,6.3) ) );
		
		Point3f pnt3f = new Point3f(-3.1f,4.2f,6.3f);
		assertTrue( pnt3f.equals( new Point3f(-3.1f,4.2f,6.3f) ) );
		
		assertTrue( pnt3f.equals( pnt3d.toFloat()) );
		
		Point3i pnt3i = new Point3i(-3,4,6);
		assertTrue( pnt3i.equals( new Point3i(-3,4,6) ) );
	}

}
