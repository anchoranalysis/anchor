/*-
 * #%L
 * anchor-io-generator
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

package org.anchoranalysis.image.io.stack.output.generator;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Allows us to call an {@code RasterGenerator<S>} as if it was an {@code RasterGenerator<T>} using
 * an function to connect the two.
 *
 * @author Owen Feehan
 * @param <T> exposed-iterator type
 * @param <V> delegate-iterator-type (hidden type)
 */
public class RasterGeneratorBridge<T, V> extends RasterGeneratorDelegateToRaster<V, T> {

    // START REQUIRED ARGUMENTS
    /**
     * The function that translates an element of type {@code T} to an element of type {@code V} for
     * the bridge.
     */
    private final CheckedFunction<T, V, ? extends Throwable> elementBridge;

    // END REQUIRED ARGUMENTS

    /**
     * Creates with a delegate and a function to bridge the exposed element-type to the delegate
     * element-type.
     *
     * @param delegate the delegate {@link RasterGenerator} which is called after bridging.
     * @param elementBridge the function that bridges the exposed element-type to the delegate
     *     element-type.
     */
    public RasterGeneratorBridge(
            RasterGenerator<V> delegate, CheckedFunction<T, V, ? extends Throwable> elementBridge) {
        super(delegate);
        this.elementBridge = elementBridge;
    }

    @Override
    public Stack transform(T element) throws OutputWriteFailedException {
        try {
            return getDelegate().transform(convertBeforeAssign(element));
        } catch (OperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    protected V convertBeforeAssign(T element) throws OperationFailedException {
        try {
            return elementBridge.apply(element);
        } catch (Exception e) {
            throw new OperationFailedException(e);
        }
    }

    @Override
    protected Stack convertBeforeTransform(Stack stack) {
        return stack;
    }
}
