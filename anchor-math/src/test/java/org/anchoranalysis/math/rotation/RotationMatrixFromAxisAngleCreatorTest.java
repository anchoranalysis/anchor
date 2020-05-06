package org.anchoranalysis.math.rotation;

/*-
 * #%L
 * anchor-math
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

import static org.junit.Assert.*;

import org.anchoranalysis.core.geometry.Vector3d;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RotationMatrixFromAxisAngleCreatorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		RotationMatrixFromAxisAngleCreator rmc = new RotationMatrixFromAxisAngleCreator(
			new Vector3d(-0.866,-0.5,2.31e-014),
			3
		);
		RotationMatrix rm = rmc.createRotationMatrix();
		
		double delta = 1e-3;
		
		// First Row
		assertEquals(  0.502414, rm.getMatrix().get(0, 0), delta );
		assertEquals(  0.861667, rm.getMatrix().get(0, 1), delta );
		assertEquals(  -0.07056, rm.getMatrix().get(0, 2), delta );
		
		// Second Row
		assertEquals(  0.861667, rm.getMatrix().get(1, 0), delta );
		assertEquals( -0.492494, rm.getMatrix().get(1, 1), delta );
		assertEquals( 0.122201, rm.getMatrix().get(1, 2), delta );
		
		// Third Row
		assertEquals( 0.07056, rm.getMatrix().get(2, 0), delta );
		assertEquals( - 0.12221, rm.getMatrix().get(2, 1), delta );
		assertEquals( -0.98999, rm.getMatrix().get(2, 2), delta );
	}

}
