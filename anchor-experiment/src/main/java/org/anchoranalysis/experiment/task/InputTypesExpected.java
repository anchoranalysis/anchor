/* (C)2020 */
package org.anchoranalysis.experiment.task;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.io.input.InputFromManager;

/**
 * A list of base (parent) classes for input-types that a task expects
 *
 * @author Owen Feehan
 */
public class InputTypesExpected {

    private List<Class<? extends InputFromManager>> list = new ArrayList<>();

    public InputTypesExpected(Class<? extends InputFromManager> cls) {
        list.add(cls);
    }

    /**
     * Does the childclass inherit from any one of expected input-types
     *
     * @param childClass the class to test if it inherits
     * @return true if inherits from at least one input-type, false otherwise
     */
    public boolean doesClassInheritFromAny(Class<? extends InputFromManager> childClass) {
        for (Class<? extends InputFromManager> possibleInputTypeClass : list) {
            if (possibleInputTypeClass.isAssignableFrom(childClass)) {
                return true;
            }
        }
        return false;
    }

    public boolean add(Class<? extends InputFromManager> e) {
        return list.add(e);
    }

    @Override
    public String toString() {
        // Describe the classes in the list
        return list.toString();
    }
}
