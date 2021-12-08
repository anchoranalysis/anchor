/*-
 * #%L
 * anchor-plugin-opencv
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
package org.anchoranalysis.image.inference.bean.segment.instance;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.inference.ImageInferenceContext;
import org.anchoranalysis.image.inference.ImageInferenceModel;
import org.anchoranalysis.image.inference.segment.LabelledWithConfidence;
import org.anchoranalysis.image.inference.segment.MultiScaleObject;
import org.anchoranalysis.inference.concurrency.ConcurrentModel;
import org.anchoranalysis.inference.concurrency.ConcurrentModelException;
import org.anchoranalysis.inference.concurrency.ConcurrentModelPool;

/**
 * Helps perform inference on an image.
 *
 * @param <T> tensor-type.
 * @param <S> model-type.
 */
@RequiredArgsConstructor
class InferenceHelper<T, S extends ImageInferenceModel<T>> {

    /** Decodes inference output into segmented objects. */
    private final DecodeInstanceSegmentation<T> decode;

    /** The name of the input in the model. */
    private final String inputName;

    /**
     * Performs inference, and decodes the outputted tensors into segmented-objects.
     *
     * @param inputTensor the tensor on which inferencei s performed.
     * @param modelPool the models used for CNN inference
     * @param context the context of the inference
     * @return the results of the segmentation
     * @throws Throwable if thrown by the inference while executing on a CPU. It is suppressed if
     *     thrown on a GPU.
     */
    public List<LabelledWithConfidence<MultiScaleObject>> queueInference(
            T inputTensor, ConcurrentModelPool<S> modelPool, ImageInferenceContext context)
            throws Throwable {
        return modelPool.executeOrWait(model -> performInference(model, inputTensor, context));
    }

    /** Performs inference on an {@code image} using {@code model}. */
    private List<LabelledWithConfidence<MultiScaleObject>> performInference(
            ConcurrentModel<S> model, T inputTensor, ImageInferenceContext context)
            throws ConcurrentModelException {
        try {
            InferenceExecutionTimeRecorder recorder =
                    new InferenceExecutionTimeRecorder(
                            context.getExecutionTimeRecorder(), model.isGpu());

            recorder.recordStartInference();

            try {
                return model.getModel()
                        .performInference(
                                inputTensor,
                                inputName,
                                decode.expectedOutputs(),
                                outputs -> decodeOutputs(outputs, recorder, context));
            } finally {
                recorder.flush();
            }

        } catch (Exception e) {
            throw new ConcurrentModelException(e);
        }
    }

    private List<LabelledWithConfidence<MultiScaleObject>> decodeOutputs(
            List<T> outputsToDecode,
            InferenceExecutionTimeRecorder recorder,
            ImageInferenceContext context)
            throws OperationFailedException {
        try {
            recorder.recordStartPost();
            return decode.decode(outputsToDecode, context);
        } finally {
            recorder.recordEndPost();
        }
    }
}
