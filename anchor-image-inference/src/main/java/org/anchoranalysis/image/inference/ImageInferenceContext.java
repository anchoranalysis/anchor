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
package org.anchoranalysis.image.inference;

import java.util.List;
import java.util.Optional;
import lombok.Value;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.inference.segment.DualScale;
import org.anchoranalysis.image.voxel.resizer.VoxelsResizer;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * Attributes describing the situation in which inference on images is occurring.
 *
 * <p>These may be helpful for decoding output from the inference.
 *
 * @author Owen Feehan
 */
@Value
public class ImageInferenceContext {

    /**
     * The size of the image for which we wish to segment, before and after any scaling for model
     * inference.
     */
    private final DualScale<Dimensions> dimensions;

    /**
     * The inverse of the scaling-factor applied to reduce {@code unscaledDimensions} to the
     * input-matrix used for inference.
     */
    private final ScaleFactor scaleFactor;

    /**
     * The scaling-factors needed to upscale the model output to match the desired scale.
     *
     * @return each scaling-factor (if required) to scale an image (at the respective scale in
     *     {@link DualScale} by, so they become identically sized to the {@link
     *     DualScale#atInputScale}.
     */
    public final DualScale<Optional<ScaleFactor>> scaleFactorUpscale() {
        return new DualScale<>(Optional.of(scaleFactor), Optional.empty());
    }

    /** If available, labels of classes loaded from a text file at {@code classLabelsPath}. */
    private final Optional<List<String>> classLabels;

    /** How to resize images or voxel-buffers. */
    private final VoxelsResizer resizer;

    /** Allows execution-time for particular operations to be recorded. */
    private final ExecutionTimeRecorder executionTimeRecorder;

    /** Where to log information messages during inference. */
    private final Logger logger;
}
