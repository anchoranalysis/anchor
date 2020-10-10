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

import java.util.Optional;
import org.anchoranalysis.io.manifest.sequencetype.IncrementingIntegers;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.recorded.RecordingWriters;

/**
 * A sequence of outputs that use the same generator, where each output increments an index by one in the filename.
 * 
 * @author Owen Feehan
 *
 * @param <T> element-type in generator
 */
public class OutputSequenceIncrementing<T> implements OutputSequence {

    private OutputSequenceIndexed<T,Integer> delegate;

    private int iteration = 0;

    // User-specified ManifestDescription for the folder
    OutputSequenceIncrementing(
            BoundOutputter<T> parameters,
            int startIndex
    ) throws OutputWriteFailedException {
        delegate = new OutputSequenceIndexed<>(parameters, new IncrementingIntegers(startIndex));
    }

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

    @Override
    public void close() throws OutputWriteFailedException {
        delegate.close();
    }

    @Override
    public boolean isOn() {
        return delegate.isOn();
    }

    @Override
    public Optional<RecordingWriters> writers() {
        return delegate.writers();
    }
}
