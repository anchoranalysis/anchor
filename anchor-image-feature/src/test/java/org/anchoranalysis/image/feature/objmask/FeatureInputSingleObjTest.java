/* (C)2020 */
package org.anchoranalysis.image.feature.objmask;

import static org.junit.Assert.*;

import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.object.ObjectMask;
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

        assertTrue(createInput(nrgStack).equals(createInput(nrgStack)));
    }

    @Test
    public void testEquals_DifferentNRGStack() {

        NRGStackWithParams nrgStack1 = Mockito.mock(NRGStackWithParams.class);
        NRGStackWithParams nrgStack2 = Mockito.mock(NRGStackWithParams.class);

        assertFalse(createInput(nrgStack1).equals(createInput(nrgStack2)));
    }

    @Test
    public void testEquals_DifferentObjects() {

        NRGStackWithParams nrgStack = Mockito.mock(NRGStackWithParams.class);

        assertFalse(createInputWithNewObj(nrgStack).equals(createInputWithNewObj(nrgStack)));
    }

    private FeatureInputSingleObject createInput(NRGStackWithParams nrgStack) {
        return new FeatureInputSingleObject(obj, nrgStack);
    }

    private FeatureInputSingleObject createInputWithNewObj(NRGStackWithParams nrgStack) {
        return new FeatureInputSingleObject(Mockito.mock(ObjectMask.class), nrgStack);
    }
}
