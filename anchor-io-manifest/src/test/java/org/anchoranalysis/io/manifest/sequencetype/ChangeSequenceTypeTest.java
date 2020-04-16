package org.anchoranalysis.io.manifest.sequencetype;

/*
 * #%L
 * anchor-io
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

public class ChangeSequenceTypeTest {

	@Test
	public void testNextIndex() throws SequenceTypeException {
		
		ChangeSequenceType cst = new ChangeSequenceType();
		
		cst.update("0");
		cst.update("5");
		cst.update("9");
		cst.update("13");
		
		assertTrue( cst.nextIndex(5)==9 );
		assertTrue( cst.nextIndex(0)==5 );
		assertTrue( cst.nextIndex(13)==-1 );
	}

	@Test
	public void testPreviousIndex() throws SequenceTypeException {
		
		ChangeSequenceType cst = new ChangeSequenceType();
		
		cst.update("0");
		cst.update("5");
		cst.update("9");
		cst.update("13");
		
		assertTrue( cst.previousIndex(5)==0 );
		assertTrue( cst.previousIndex(0)==-1 );
		assertTrue( cst.previousIndex(13)==9 );
	}

}
