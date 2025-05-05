/*-
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2010 - 2025 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.experiment.bean.processor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.anchoranalysis.io.input.InputFromManager;

/** Creates mock inputs for testing. */
class MockInputFixture {

    /**
     * Creates an immutable list of inputs.
     *
     * @param count the number of inputs to create.
     * @return an immutable list containing the created inputs
     */
    public static List<InputFromManager> createInputs(int count) {
        List<InputFromManager> inputs = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            inputs.add(createInput(index));
        }
        return Collections.unmodifiableList(inputs);
    }

    private static InputFromManager createInput(int index) {
        InputFromManager mockInput = mock(InputFromManager.class);
        when(mockInput.identifier()).thenReturn("input_" + index);
        return mockInput;
    }
}
