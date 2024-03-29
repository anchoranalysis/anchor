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

package org.anchoranalysis.io.generator.sequence;

import org.anchoranalysis.core.functional.checked.CheckedRunnable;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * A sequence of outputs that use the same generator, where each output increments an index by one
 * in the filename.
 *
 * @author Owen Feehan
 * @param <T> element-type in generator
 */
public class OutputSequenceIncrementing<T> {

    private OutputSequenceIndexed<T, Integer> delegate;

    private int iteration = 0;

    /**
     * Create with an outputter.
     *
     * @param outputter the outputter.
     * @throws OutputWriteFailedException if missing ingredients for outputting, or if unable to
     *     initialize.
     */
    public OutputSequenceIncrementing(BoundOutputter<T> outputter)
            throws OutputWriteFailedException {
        delegate = new OutputSequenceIndexed<>(outputter);
    }

    /**
     * Outputs an additional element in the sequence.
     *
     * <p>This method is <i>thread-safe</i>.
     *
     * @param element the element to add.
     * @throws OutputWriteFailedException if the output cannot be successfully written.
     */
    public void add(T element) throws OutputWriteFailedException {
        try {
            delegate.add(element, iteration);
        } finally {
            // We always update the index, even if an exception occurred. This is because:
            //  1. By design, it shouldn't change the intended indexing.
            //  2. A downstream generator has already updated its sequence-type.
            iteration++;
        }
    }

    /**
     * Like {@link #add(Object)} but does not immediately execute the underlying add operation,
     * instead returning an operation that can be called later.
     *
     * <p>This is useful for separating the sequential (and therefore not thread-safe, and must be
     * synchronized) from the actual write operation (which must not be synchronized, and is usually
     * more expensive).
     *
     * <p>This method is <i>thread-safe</i>.
     *
     * @param element the element to add.
     * @return a runnable, that when called, completes the remain part of the operation.
     * @throws OutputWriteFailedException if the output cannot be successfully written.
     */
    public CheckedRunnable<OutputWriteFailedException> addAsynchronously(T element)
            throws OutputWriteFailedException {
        final int iterationBefore = iteration;
        iteration++;
        return () -> delegate.add(element, iterationBefore);
    }
}
