package org.anchoranalysis.image.feature.objmask;

import static org.junit.Assert.*;

import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.objmask.ObjMask;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class FeatureInputSingleObjTest {

	private ObjMask obj;
	
	@Before
	public void setup() {
		// An arbitrary object
		obj = Mockito.mock(ObjMask.class);
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
			Mockito.mock(ObjMask.class),
			nrgStack
		);
	}
}
