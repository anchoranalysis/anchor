/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.core.stack.named;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.stack.Stack;

/**
 * Like {@link NamedStacks} but enforces a condition that all stacks must have the same dimensions.
 *
 * @author Owen Feehan
 */
public class NamedStacksUniformSize {

    /** Lazy initialization after first stack is added */
    private Dimensions dimensions;

    private NamedStacks delegate = new NamedStacks();

    /**
     * Adds an element to the store.
     *
     * @param identifier a unique identifier for the element.
     * @param stack the stack to add.
     * @throws OperationFailedException if the identifier already exists, or otherwise the add
     *     operation fails.
     */
    public void add(String identifier, Stack stack) throws OperationFailedException {

        if (dimensions == null) {
            dimensions = stack.dimensions();
        } else {
            if (!stack.dimensions().equals(dimensions)) {
                throw new OperationFailedException("Stack dimensions do not match");
            }
        }

        delegate.add(identifier, () -> stack);
    }

    /**
     * Exposes the stacks as a {@link NamedStacks}.
     *
     * @return an interface as {@link NamedStacks}.
     */
    public NamedStacks asNamedStacks() {
        return delegate;
    }
}
