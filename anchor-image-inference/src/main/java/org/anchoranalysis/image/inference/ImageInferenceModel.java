/*-
 * #%L
 * anchor-plugin-image
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
package org.anchoranalysis.image.inference;

import java.util.List;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.inference.InferenceModel;

/**
 * A model used for inference that accepts an image as an input.
 *
 * <p>A model should always be closed, when it is no longer in use.
 *
 * @author Owen Feehan
 * @param <T> tensor-type that is both inputted to (representing an image), and outputted from the
 *     model during inference.
 */
public interface ImageInferenceModel<T> extends InferenceModel {

    /**
     * Performs inference on a single-input, to create an output.
     *
     * @param <S> the data-type the output is exposed as.
     * @param input the input for inference.
     * @param inputName the name associated with {@code input} in the model.
     * @param outputIdentifiers
     * @param convertOutput converts the output to type {@code <S>}.
     * @return the converted output.
     * @throws OperationFailedException if the inference cannot successfully complete.
     */
    <S> S performInference(
            T input,
            String inputName,
            List<String> outputIdentifiers,
            CheckedFunction<List<T>, S, OperationFailedException> convertOutput)
            throws OperationFailedException;
}
