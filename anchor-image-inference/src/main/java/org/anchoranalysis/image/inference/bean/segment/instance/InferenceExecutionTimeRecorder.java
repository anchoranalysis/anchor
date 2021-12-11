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

import java.util.Optional;
import org.anchoranalysis.core.functional.OptionalFactory;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;

/**
 * Records the execution-time of several operations during inference.
 *
 * <p>It expects all methods to be called once, in a defined order.
 *
 * <ol>
 *   <li>{@code recordStartInference()}
 *   <li>{@code recordStartPost()}
 *   <li>{@code recordEndPost()}
 *   <li>{@code flush()}
 * </ol>
 *
 * @author Owen Feehan
 */
class InferenceExecutionTimeRecorder {

    /** Unique identifier for recording post-processing operations after model inference. */
    private static final String IDENTIFIER_POST = "Post-processing inference";

    /** The underlying {@link ExecutionTimeRecorder} used for recording times. */
    private final ExecutionTimeRecorder recorder;

    /**
     * Unique identifier for recording inference operation during the GPU warm-up (if it occurs).
     */
    private final Optional<String> identifierInferenceWarmUp;

    /** Unique identifier for recording subsequent inference operations, CPU or GPU. */
    private final String identifierInferenceSubsequent;

    /** A timestamp for when the inference starts. */
    private long timestampStartInference;

    /** A timestamp for when inference ends and post-processing starts. */
    private long timestampStartPostProcessing;

    /** A timestamp for when post-processing ends. */
    private long timestampEndPostProcessing;

    /**
     * Creates for a particular {@link ExecutionTimeRecorder}.
     *
     * @param recorder the recorder.
     * @param gpu whether the inference uses a GPU or not.
     */
    public InferenceExecutionTimeRecorder(ExecutionTimeRecorder recorder, boolean gpu) {
        this.recorder = recorder;
        this.identifierInferenceWarmUp =
                OptionalFactory.create(gpu, "Model inference (GPU with Warm Up)");
        this.identifierInferenceSubsequent = identifierSubsequent(gpu);
    }

    /** Record the current time as the <b>start of inference</b>. */
    public void recordStartInference() {
        this.timestampStartInference =
                recorder.measureTime(true, allPossibleInferenceIdentifiers());
    }

    /** Record the current time as the <b>end of inference</b>, and the start of post-processing. */
    public void recordStartPost() {
        this.timestampStartPostProcessing = recorder.measureTime(true, IDENTIFIER_POST);
    }

    /** Record the current time as the end of post-processing. */
    public void recordEndPost() {
        this.timestampEndPostProcessing = recorder.measureTime(false, IDENTIFIER_POST);
    }

    /** Called after recording all timestamps, to record appropriate entries in {@code recorder}. */
    public void flush() {
        recorder.recordTimeDifferenceBetween(
                inferenceIdentifier(), timestampStartInference, timestampStartPostProcessing);
        recorder.recordTimeDifferenceBetween(
                IDENTIFIER_POST, timestampStartPostProcessing, timestampEndPostProcessing);
    }

    /**
     * The identifier to use for measuring inference, which may toggle between a GPU (warm up) or
     * not.
     */
    private String inferenceIdentifier() {
        if (identifierInferenceWarmUp.isPresent()
                && !recorder.isOperationAlreadyRecorded(identifierInferenceWarmUp.get())) {
            return identifierInferenceWarmUp.get();
        } else {
            return identifierInferenceSubsequent;
        }
    }

    /** All possible inference identifiers that may be used. */
    private String[] allPossibleInferenceIdentifiers() {
        if (identifierInferenceWarmUp.isPresent()) {
            return new String[] {
                identifierInferenceWarmUp.get(),
                identifierSubsequent(true),
                identifierSubsequent(false)
            };
        } else {
            return new String[] {identifierSubsequent(false)};
        }
    }

    /** The identifier that is used for subsequent operations. */
    private static String identifierSubsequent(boolean gpu) {
        return addSuffix("Model inference", gpu);
    }

    /** Adds a suffix indicating if a CPU or GPU was used. */
    private static String addSuffix(String string, boolean gpu) {
        return String.format("%s (%s)", string, gpu ? "GPU" : "CPU");
    }
}
