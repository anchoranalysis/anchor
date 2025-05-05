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
