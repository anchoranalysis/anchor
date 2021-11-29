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
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.image.bean.nonbean.error.SegmentationFailedException;
import org.anchoranalysis.image.bean.spatial.ScaleCalculator;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.inference.ImageInferenceContext;
import org.anchoranalysis.image.inference.ImageInferenceModel;
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

        stack = checkAndCorrectInput(stack);

        try {
            ScaleFactor downfactor =
                    scaleInput.calculate(Optional.of(stack.dimensions()), Optional.empty());

            ScaleFactor upfactor = downfactor.invert();

            Optional<double[]> subtractMeans = subtractMeanArray(stack.getNumberChannels());

            T input = deriveInput(stack, downfactor, subtractMeans);

            InferenceHelper<T, S> helper =
                    new InferenceHelper<>(decode, inputName().orElse(FALLBACK_INPUT_NAME));
            return helper.queueInference(
                    input,
                    modelPool,
                    new ImageInferenceContext(
                            stack.dimensions(),
                            upfactor,
                            classLabels(),
                            executionTimeRecorder,
                            getInitialization().getSharedObjects().getContext().getLogger()));
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
     * @param downfactor how much to scale the size of {@code image} down by, to form an input
     *     tensor.
     * @param subtractMeans respective intensity values that are subtracted from the voxels before
     *     being added to the tensor (respectively for each channel).
     * @return the tensor, representing the input image.
     * @throws OperationFailedException if an input tensor cannot be created.
     */
    protected abstract T deriveInput(
            Stack stack, ScaleFactor downfactor, Optional<double[]> subtractMeans)
            throws OperationFailedException;

    /**
     * The name of the tensor in the model which the input-image is mapped to.
     *
     * @return the name.
     */
    protected abstract Optional<String> inputName();

    @SuppressWarnings("unchecked")
    private Optional<double[]> subtractMeanArray(int numberChannels)
            throws SegmentationFailedException {

        if (subtractMean != null) {
            List<Double> list =
                    (List<Double>) IteratorUtils.toList(subtractMean.iterator()); // NOSONAR

            if (list.size() != numberChannels) {
                throw new SegmentationFailedException(
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

    /** Checks the input-stack has the necessary number of channels, otherwise throwing an error. */
    private Stack checkAndCorrectInput(Stack stack) throws SegmentationFailedException {
        if (stack.getNumberChannels() == 1) {
            return checkInput(grayscaleToRGB(stack.getChannel(0)));
        } else {
            return checkInput(stack);
        }
    }

    private Stack checkInput(Stack stack) throws SegmentationFailedException {
        if (stack.getNumberChannels() != 3) {
            throw new SegmentationFailedException(
                    String.format(
                            "Non-RGB stacks are not supported by this algorithm. This stack has %d channels.",
                            stack.getNumberChannels()));
        }

        if (stack.dimensions().z() > 1) {
            throw new SegmentationFailedException("z-stacks are not supported by this algorithm");
        }

        return stack;
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

    private static Stack grayscaleToRGB(Channel channel) {
        try {
            return new Stack(true, channel, channel.duplicate(), channel.duplicate());
        } catch (IncorrectImageSizeException | CreateException e) {
            throw new AnchorImpossibleSituationException();
        }
    }
}
