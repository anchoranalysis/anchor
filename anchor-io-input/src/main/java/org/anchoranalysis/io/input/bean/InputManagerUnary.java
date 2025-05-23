/*-
 * #%L
 * anchor-io-input
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.io.input.bean;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.input.InputsWithDirectory;

/**
 * Base class for an {@link InputManager} that delegates to another {@link InputManager} with the
 * same input-type.
 *
 * @author Owen Feehan
 * @param <T> input-type
 */
public abstract class InputManagerUnary<T extends InputFromManager> extends InputManager<T> {

    // START BEAN PROPERTIES
    /**
     * The delegate input-manager which will be circumstantially called by the current
     * input-manager.
     */
    @BeanField @Getter @Setter private InputManager<T> input;

    // END BEAN PROPERITES

    @Override
    public InputsWithDirectory<T> inputs(InputManagerParameters parameters)
            throws InputReadFailedException {
        return inputsFromDelegate(input.inputs(parameters), parameters);
    }

    /**
     * Calculates the inputs to return given the inputs from the delegate.
     *
     * @param fromDelegate the inputs from the delegate.
     * @param parameters parameters for determining inputs.
     * @return inputs to return after any further processing.
     * @throws InputReadFailedException if inputs cannot be successfully read from the file-system.
     */
    protected abstract InputsWithDirectory<T> inputsFromDelegate(
            InputsWithDirectory<T> fromDelegate, InputManagerParameters parameters)
            throws InputReadFailedException;
}
