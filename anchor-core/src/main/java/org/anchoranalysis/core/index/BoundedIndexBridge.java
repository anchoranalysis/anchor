package org.anchoranalysis.core.index;

/*-
 * #%L
 * anchor-core
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

import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
public class BoundedIndexBridge<T>
        implements FunctionWithException<Integer, T, GetOperationFailedException> {

    /** The container associated with the bridge */
    @Setter
    private BoundedIndexContainer<T> container;

    @Override
    public T apply(Integer sourceObject) throws GetOperationFailedException {
        int index = container.previousEqualIndex(sourceObject);
        if (index == -1) {
            throw new GetOperationFailedException(sourceObject, "Cannot find a previousEqualIndex in the cntr");
        }
        return container.get(index);
    }
}
