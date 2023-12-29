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
package org.anchoranalysis.io.generator;

import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Performs preprocessing to transform the element into another type before being written to the
 * filesystem.
 *
 * @author Owen Feehan
 * @param <T> iteration-type
 * @param <S> type after any necessary preprocessing
 */
public interface TransformingGenerator<T, S> extends Generator<T> {

    /**
     * Applies any necessary preprocessing to create an element suitable for writing to the
     * filesystem.
     *
     * @param element element to be assigned and then transformed.
     * @return the transformed element after necessary preprocessing.
     * @throws OutputWriteFailedException if anything goes wrong.
     */
    S transform(T element) throws OutputWriteFailedException;
}
