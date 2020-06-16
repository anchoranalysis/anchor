package org.anchoranalysis.image.feature.objmask;

/*-
 * #%L
 * anchor-image-feature
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.objectmask.ObjectMask;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class FeatureInputSingleObjTest {

	private ObjectMask obj;
	
	@Before
	public void setup() {
		// An arbitrary object
		obj = Mockito.mock(ObjectMask.class);
	}
	
	@Test
	public void testEquals_SameNRGStack() {

		NRGStackWithParams nrgStack = Mockito.mock(NRGStackWithParams.class);
		
		assertTrue(
			createInput(nrgStack).equals(
				createInput(nrgStack)
			)
		);
	}
	
	@Test
	public void testEquals_DifferentNRGStack() {

		NRGStackWithParams nrgStack1 = Mockito.mock(NRGStackWithParams.class);
		NRGStackWithParams nrgStack2 = Mockito.mock(NRGStackWithParams.class);
		
		assertFalse(
			createInput(nrgStack1).equals(
				createInput(nrgStack2)
			)
		);
	}
	
	@Test
	public void testEquals_DifferentObjs() {

		NRGStackWithParams nrgStack = Mockito.mock(NRGStackWithParams.class);
		
		assertFalse(
			createInputWithNewObj(nrgStack).equals(
				createInputWithNewObj(nrgStack)
			)
		);
	}

	private FeatureInputSingleObj createInput( NRGStackWithParams nrgStack ) {
		return new FeatureInputSingleObj(obj,nrgStack);
	}
	
	private FeatureInputSingleObj createInputWithNewObj( NRGStackWithParams nrgStack ) {
		return new FeatureInputSingleObj(
			Mockito.mock(ObjectMask.class),
			nrgStack
		);
	}
}
