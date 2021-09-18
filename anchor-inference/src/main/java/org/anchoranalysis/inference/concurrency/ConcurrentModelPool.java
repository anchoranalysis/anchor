/*-
 * #%L
 * anchor-core
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
package org.anchoranalysis.inference.concurrency;

import java.util.concurrent.PriorityBlockingQueue;
import org.anchoranalysis.core.functional.FunctionalIterate;
import org.anchoranalysis.core.functional.checked.CheckedFunction;

/**
 * Keeps concurrent copies of a model to be used by different threads.
 *
 * <p>The copies van variously use GPU and CPU for execution, with GPU always being given priority.
 *
 * @author Owen Feehan
 * @param <T> model-type
 */
public class ConcurrentModelPool<T> {

    /**
     * Creates a model to use in the pool.
     *
     * @author Owen Feehan
     * @param <T> model-type
     */
    public interface CreateModelForPool<T> {

        /**
         * Creates a model.
         *
         * @param useGPU whether to use a GPU if possible (if not possible, revert to CPU)
         * @return the newly created model
         * @throws CreateModelFailedException if the model cannot be successfully created.
         */
        public ConcurrentModel<T> create(boolean useGPU) throws CreateModelFailedException;
    }

    /**
     * A queue that prioritizes if {@code hasPriority==true} and blocks if {@link
     * PriorityBlockingQueue#take} is called but no elements are available.
     */
    private PriorityBlockingQueue<WithPriority<ConcurrentModel<T>>> queue;

    /** Function to create a model. */
    private final CreateModelForPool<T> createModel;

    /**
     * Creates with a particular plan and function to create models.
     *
     * @param plan a plan determining how many CPUs and GPUs to use for inference.
     * @param createModel called to create a new model, as needed.
     * @throws CreateModelFailedException if a model cannot be created.
     */
    public ConcurrentModelPool(ConcurrencyPlan plan, CreateModelForPool<T> createModel)
            throws CreateModelFailedException {
        this.createModel = createModel;
        this.queue = new PriorityBlockingQueue<>();

        addNumberModels(plan.numberGPUs(), true, createModel);

        addNumberModels(plan.numberCPUs() - plan.numberGPUs(), false, createModel);
    }

    /**
     * Execute on the next available model (or wait until one becomes available).
     *
     * <p>If an exception is thrown while executing on a GPU, the GPU processor is no longer used,
     * and instead an additional CPU node is added. The failed job is tried again.
     *
     * @param functionToExecute function to execute on a given model, possibly throwing an
     *     exception.
     * @param <S> return type
     * @return the value returned by {@code functionToExecute} after it is executed.
     * @throws Throwable if thrown from {@code functionToExecute} while executing on a CPU. It is
     *     suppressed if thrown on a GPU.
     */
    public <S> S executeOrWait(
            CheckedFunction<ConcurrentModel<T>, S, ConcurrentModelException> functionToExecute)
            throws Throwable { // NOSONAR
        while (true) {
            WithPriority<ConcurrentModel<T>> model = getOrWait();
            try {
                S returnValue = functionToExecute.apply(model.get());

                // When finished executing without error we return the model to the pool
                giveBack(model);
                return returnValue;
            } catch (ConcurrentModelException e) {
                if (model.isGPU()) {
                    // Add extra CPU model, and try to execute the function again
                    addNumberModels(1, false, createModel);
                } else {
                    // Rethrow if error occurred on a CPU
                    throw e.getCause();
                }
            }
        }
    }

    /**
     * Gets an instantiated model to be used.
     *
     * <p>After usage, {@link #giveBack} should be called to return the model to the pool.
     *
     * @return the model to be used concurrently, with an associated priority.
     * @throws InterruptedException
     */
    private WithPriority<ConcurrentModel<T>> getOrWait() throws InterruptedException {
        return queue.take();
    }

    /**
     * Returns the model to the pool.
     *
     * @param model the model to return
     */
    private void giveBack(WithPriority<ConcurrentModel<T>> model) {
        queue.put(model);
    }

    private void addNumberModels(
            int numberModels, boolean useGPU, CreateModelForPool<T> createModel)
            throws CreateModelFailedException {
        FunctionalIterate.repeat(numberModels, () -> queue.add(create(useGPU, createModel)));
    }

    private WithPriority<ConcurrentModel<T>> create(
            boolean useGPU, CreateModelForPool<T> createModel) throws CreateModelFailedException {
        return new WithPriority<>(createModel.create(useGPU), useGPU);
    }
}
