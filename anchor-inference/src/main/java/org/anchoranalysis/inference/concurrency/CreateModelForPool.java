/*-
 * #%L
 * anchor-inference
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
package org.anchoranalysis.inference.concurrency;

import java.util.Optional;
import org.anchoranalysis.inference.InferenceModel;

/**
 * Creates a model to use in the pool.
 *
 * @author Owen Feehan
 * @param <T> model-type
 */
public interface CreateModelForPool<T extends InferenceModel> {

    /**
     * Creates a model.
     *
     * @param useGPU whether to use a GPU if possible (if not possible, revert to CPU).
     * @return the newly created model, if possible.
     * @throws CreateModelFailedException if something unexpected happened. If a GPU is unavailable,
     *     prefer to return {@link Optional#empty}.
     */
    public Optional<ConcurrentModel<T>> create(boolean useGPU) throws CreateModelFailedException;
}
