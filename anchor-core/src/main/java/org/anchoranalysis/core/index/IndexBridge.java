package org.anchoranalysis.core.index;

import org.anchoranalysis.core.functional.checked.CheckedFunction;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IndexBridge<T> implements CheckedFunction<Integer, T, GetOperationFailedException> {

    private GetterFromIndex<T> getter;

    @Override
    public T apply(Integer sourceObject) throws GetOperationFailedException {
        return getter.get(sourceObject);
    }
}
