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
package org.anchoranalysis.image.inference.bean.segment.instance;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.primitive.DoubleList;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.image.bean.nonbean.error.SegmentationFailedException;
import org.anchoranalysis.image.bean.spatial.ScaleCalculator;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.inference.ImageInferenceContext;
import org.anchoranalysis.image.inference.ImageInferenceModel;
import org.anchoranalysis.image.inference.segment.DualScale;
import org.anchoranalysis.image.inference.segment.LabelledWithConfidence;
import org.anchoranalysis.image.inference.segment.MultiScaleObject;
import org.anchoranalysis.image.inference.segment.SegmentedObjects;
import org.anchoranalysis.inference.concurrency.ConcurrentModelPool;
import org.anchoranalysis.io.manifest.file.TextFileReader;
import org.anchoranalysis.spatial.scale.ScaleFactor;
import org.apache.commons.collections.IteratorUtils;

/**
 * A {@link SegmentStackIntoObjectsScaleDecode} that scales the input image, before performing
 * inference, and then decodes the output.
 *
 * @author Owen Feehan
 * @param <T> tensor-type inputted and outputted to model
 * @param <S> model-type
 */
public abstract class SegmentStackIntoObjectsScaleDecode<T, S extends ImageInferenceModel<T>>
        extends SegmentStackIntoObjectsPooled<S> {

    private static final String FALLBACK_INPUT_NAME = "no_name_defined";

    // START BEAN PROPERTIES
    /**
     * Any scaling to be applied to the input-image before being input to the model for inference.
     */
    @BeanField @Getter @Setter private ScaleCalculator scaleInput;

    /** Decodes inference output into segmented objects. */
    @BeanField @Getter @Setter private DecodeInstanceSegmentation<T> decode;

    /**
     * A constant intensity for each respective channel to be subtracted before performing
     * inference.
     *
     * <p>If set, this should create an list, with as many elements as channels inputted to the
     * inference model.
     */
    @BeanField @OptionalBean @Getter @Setter private DoubleList subtractMean;

    /**
     * Relative-path to the class-labels file, a text file where each line specifies a class label
     * in order, relative to the <i>models/</i> directory in the Anchor distribution.
     *
     * <p>If empty, then no such file is specified.
     */
    @BeanField @Getter @Setter @AllowEmpty private String classLabelsPath = "";
    // END BEAN PROPERTIES

    @Override
    public SegmentedObjects segment(
            Stack stack,
            ConcurrentModelPool<S> modelPool,
            ExecutionTimeRecorder executionTimeRecorder)
            throws SegmentationFailedException {

        try {
            ScaleFactor scaleFactor =
                    scaleInput.calculate(Optional.of(stack.dimensions()), Optional.empty());

            DualScale<Stack> stacksDual =
                    executionTimeRecorder.recordExecutionTime(
                            "Scaling stack to model-size",
                            () -> StackScaler.scaleToModelSize(stack, scaleFactor));

            T input =
                    executionTimeRecorder.recordExecutionTime(
                            "Deriving input for segmentation",
                            () -> deriveInputSubtractMeans(stacksDual.atModelScale()));

            return segmentInput(input, stacksDual, scaleFactor, modelPool, executionTimeRecorder);
        } catch (OperationFailedException e) {
            throw new SegmentationFailedException(e);
        }
    }

    private SegmentedObjects segmentInput(
            T input,
            DualScale<Stack> stacksDual,
            ScaleFactor scaleFactor,
            ConcurrentModelPool<S> modelPool,
            ExecutionTimeRecorder executionTimeRecorder)
            throws SegmentationFailedException {
        try {
            ImageInferenceContext context =
                    createContext(stacksDual, scaleFactor, executionTimeRecorder);

            String inputName = inputName().orElse(FALLBACK_INPUT_NAME);

            InferenceHelper<T, S> helper = new InferenceHelper<>(decode, inputName);

            List<LabelledWithConfidence<MultiScaleObject>> objects =
                    helper.queueInference(input, modelPool, context);

            return new SegmentedObjects(objects, stacksDual, executionTimeRecorder);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SegmentationFailedException(e);
        } catch (Throwable e) {
            throw new SegmentationFailedException(e);
        }
    }

    /**
     * Derives the input tensor from an image.
     *
     * @param stack the image which is mapped into an input tensor.
     * @param subtractMeans respective intensity values that are subtracted from the voxels before
     *     being added to the tensor (respectively for each channel).
     * @return the tensor, representing the input image.
     * @throws OperationFailedException if an input tensor cannot be created.
     */
    protected abstract T deriveInput(Stack stack, Optional<double[]> subtractMeans)
            throws OperationFailedException;

    /**
     * The name of the tensor in the model which the input-image is mapped to.
     *
     * @return the name.
     */
    protected abstract Optional<String> inputName();

    @SuppressWarnings("unchecked")
    private Optional<double[]> subtractMeanArray(int numberChannels)
            throws OperationFailedException {

        if (subtractMean != null) {
            List<Double> list =
                    (List<Double>) IteratorUtils.toList(subtractMean.iterator()); // NOSONAR

            if (list.size() != numberChannels) {
                throw new OperationFailedException(
                        String.format(
                                "There are %d channels in the input stack for inference, but %d constants were supplied for mean-subtraction.",
                                numberChannels, list.size()));
            }

            double[] array = list.stream().mapToDouble(Double::doubleValue).toArray();
            return Optional.of(array);
        } else {
            return Optional.empty();
        }
    }

    /** A list of ordered object-class labels, if a class-labels file is specified. */
    private Optional<List<String>> classLabels() throws IOException {
        if (!classLabelsPath.isEmpty()) {
            try {
                Path filename = resolve(classLabelsPath);
                return Optional.of(TextFileReader.readLinesAsList(filename));
            } catch (InitializeException e) {
                throw new IOException(e);
            }
        } else {
            return Optional.empty();
        }
    }

    /** Creates the {@link ImageInferenceContext} needed for inference. */
    private ImageInferenceContext createContext(
            DualScale<Stack> stacks,
            ScaleFactor scaleFactor,
            ExecutionTimeRecorder executionTimeRecorder)
            throws InitializeException, IOException {
        return new ImageInferenceContext(
                stacks.map(Stack::dimensions),
                scaleFactor.invert(),
                classLabels(),
                executionTimeRecorder,
                getInitialization().getSharedObjects().getContext().getLogger());
    }

    /**
     * Derive an input-tensor for the model, including performing any necessary mean-subtraction
     * from the {@link Channel}s.
     */
    private T deriveInputSubtractMeans(Stack stack) throws OperationFailedException {
        Optional<double[]> subtractMeans = subtractMeanArray(stack.getNumberChannels());
        return deriveInput(stack, subtractMeans);
    }
}
