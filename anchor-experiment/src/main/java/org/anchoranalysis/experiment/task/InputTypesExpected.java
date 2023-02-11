/*-
 * #%L
 * anchor-experiment
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

package org.anchoranalysis.experiment.task;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.io.input.InputFromManager;

/**
 * A list of base (parent) classes for input-types that a task expects.
 *
 * @author Owen Feehan
 */
public class InputTypesExpected {

    private List<Class<? extends InputFromManager>> list = new ArrayList<>();

    /**
     * Creates with the class of the input-type.
     *
     * @param inputTypeClass the class of the input-type.
     */
    public InputTypesExpected(Class<? extends InputFromManager> inputTypeClass) {
        list.add(inputTypeClass);
    }

    /**
     * Does the child-class inherit from any one of expected input-types?
     *
     * @param childClass the class to test if it inherits
     * @return true if inherits from at least one input-type, false otherwise
     */
    public boolean doesClassInheritFromAny(Class<? extends InputFromManager> childClass) {
        return list.stream().anyMatch(possibleInput -> possibleInput.isAssignableFrom(childClass));
    }

    /**
     * Appends an input-type class to the existing list.
     *
     * @param inputTypeClass the input-type class to append.
     */
    public void add(Class<? extends InputFromManager> inputTypeClass) {
        list.add(inputTypeClass);
    }

    @Override
    public String toString() {
        // Describe the classes in the list
        return list.toString();
    }
}
