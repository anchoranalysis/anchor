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
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.inference.ImageInferenceContext;
import org.anchoranalysis.image.inference.segment.LabelledWithConfidence;
import org.anchoranalysis.image.inference.segment.MultiScaleObject;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/**
 * Decodes inference output into segmented-objects.
 *
 * @author Owen Feehan
 * @param <T> tensor-type, depending on which framework is used.
 */
public abstract class DecodeInstanceSegmentation<T>
        extends AnchorBean<DecodeInstanceSegmentation<T>> {

    /**
     * Decodes the output tensors from inference into {@link ObjectMask}s with confidence and
     * labels.
     *
     * <p>The created {@link ObjectMask}s should match {@code unscaledDimensions} in size.
     *
     * @param inferenceOutput the tensors that are the result of the inference.
     * @param context the context in which the inference is occurring.
     * @return a newly created list of objects, with associated confidence, and labels, that matches
     *     {@code unscaledDimensions} in size.
     * @throws OperationFailedException if it cannot be decoded successfully.
     */
    public abstract List<LabelledWithConfidence<MultiScaleObject>> decode(
            List<T> inferenceOutput, ImageInferenceContext context) throws OperationFailedException;

    /**
     * Ordered names of the tensors we are interested in processing, as outputted from inference.
     *
     * @return the list of names, as above.
     */
    public abstract List<String> expectedOutputs();
}
