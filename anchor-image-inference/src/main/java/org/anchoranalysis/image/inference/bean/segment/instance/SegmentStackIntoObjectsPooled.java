/*-
 * #%L
 * anchor-image-bean
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

package org.anchoranalysis.image.inference.bean.segment.instance;

import java.nio.file.Path;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.image.bean.nonbean.error.SegmentationFailedException;
import org.anchoranalysis.image.bean.segment.SegmentationBean;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.inference.segment.SegmentedObjects;
import org.anchoranalysis.inference.InferenceModel;
import org.anchoranalysis.inference.concurrency.ConcurrencyPlan;
import org.anchoranalysis.inference.concurrency.ConcurrentModelPool;
import org.anchoranalysis.inference.concurrency.CreateModelFailedException;
import org.anchoranalysis.spatial.box.Extent;

/**
 * A base class for algorithms to segment a stack into one or more objects - using a pool of models
 *
 * <p>These models are typically <a
 * href="https://en.wikipedia.org/wiki/Convolutional_neural_network">CNN</a> deep-learning models.
 *
 * @author Owen Feehan
 * @param <T> model-type
 */
public abstract class SegmentStackIntoObjectsPooled<T extends InferenceModel>
        extends SegmentationBean<SegmentStackIntoObjectsPooled<T>> {

    /**
     * Segments individually using a pool of size 1 just for one stack.
     *
     * <p>See {@link #segment(Stack, ConcurrentModelPool, ExecutionTimeRecorder)} for more details.
     *
     * @param stack the stack to segment.
     * @param executionTimeRecorder for measuring execution-times of operations.
     * @return a collection of objects with corresponding confidence scores.
     * @throws SegmentationFailedException if anything goes wrong during the segmentation.
     */
    public SegmentedObjects segment(Stack stack, ExecutionTimeRecorder executionTimeRecorder)
            throws SegmentationFailedException {
        try {
            return segment(
                    stack,
                    createModelPool(ConcurrencyPlan.singleCPUProcessor(0), getLogger()),
                    executionTimeRecorder);
        } catch (CreateModelFailedException e) {
            throw new SegmentationFailedException("Cannot create model for segmentaiton", e);
        }
    }

    /**
     * Creates the model pool (to be used by multiple threads).
     *
     * @param plan the number and types of processors available for concurrent execution.
     * @param logger the logger.
     * @return the newly created model pool.
     * @throws CreateModelFailedException if a model cannot be created.
     */
    public abstract ConcurrentModelPool<T> createModelPool(ConcurrencyPlan plan, Logger logger)
            throws CreateModelFailedException;

    /**
     * Segments a stack to produce an object-collection.
     *
     * <p>Any created objects will always exist inside the stack's {@link Extent}.
     *
     * @param stack the stack to segment.
     * @param modelPool the pool of model instances which can each be used for inference (in
     *     parallel).
     * @param executionTimeRecorder measures execution-times of particular operations.
     * @return a collection of objects with corresponding confidence scores.
     * @throws SegmentationFailedException if anything goes wrong during the segmentation.
     */
    public abstract SegmentedObjects segment(
            Stack stack,
            ConcurrentModelPool<T> modelPool,
            ExecutionTimeRecorder executionTimeRecorder)
            throws SegmentationFailedException;

    /**
     * Resolves a relative filename for a model into a path, relative to the model directory.
     *
     * @param modelFilename the filename for the model (to the model directory).
     * @return an absolute path to the model.
     * @throws InitializeException if a bean requires initialization, but has not been initialized.
     */
    protected Path resolve(String modelFilename) throws InitializeException {
        return getInitialization().getModelDirectory().resolve(modelFilename);
    }
}
