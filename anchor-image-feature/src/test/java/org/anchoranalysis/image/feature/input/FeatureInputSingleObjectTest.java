/*-
 * #%L
 * anchor-image-feature
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.image.feature.input;

import static org.junit.jupiter.api.Assertions.*;

import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class FeatureInputSingleObjectTest {

    private ObjectMask object;

    @BeforeEach
    void setup() {
        // An arbitrary object
        object = Mockito.mock(ObjectMask.class);
    }

    @Test
    void testEquals_SameEnergyStack() {
        EnergyStack energyStack = Mockito.mock(EnergyStack.class);
        assertEquals(createInput(energyStack), createInput(energyStack));
    }

    @Test
    void testEquals_DifferentEnergyStack() {
        EnergyStack energyStack1 = Mockito.mock(EnergyStack.class);
        EnergyStack energyStack2 = Mockito.mock(EnergyStack.class);
        assertNotEquals(createInput(energyStack2), createInput(energyStack1));
    }

    @Test
    void testEquals_DifferentObjects() {
        EnergyStack energyStack = Mockito.mock(EnergyStack.class);
        assertNotEquals(
                createInputWithNewObject(energyStack), createInputWithNewObject(energyStack));
    }

    private FeatureInputSingleObject createInput(EnergyStack energyStack) {
        return new FeatureInputSingleObject(object, energyStack);
    }

    private FeatureInputSingleObject createInputWithNewObject(EnergyStack energyStack) {
        return new FeatureInputSingleObject(Mockito.mock(ObjectMask.class), energyStack);
    }
}
