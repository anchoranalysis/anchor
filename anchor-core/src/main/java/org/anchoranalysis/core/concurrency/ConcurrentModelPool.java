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
package org.anchoranalysis.core.concurrency;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Keeps concurrent copies of a model to be used by different threads.
 * 
 * <p>The copies van variously use GPU and CPU for execution, with GPU always being given
 * priority.
 * 
 * @author Owen Feehan
 * @param <T> model-type
 */
public class ConcurrentModelPool<T> {

    /**
     * Creates a model to use in the pool
     * 
     * @author Owen Feehan
     *
     * @param <T> model-type
     */
    public interface CreateModelForPool<T> {
        
        /**
         * Creates a model
         * @param useGPU whether to use a GPU if possible (if not possible, revert to CPU)
         * @return the newly created model
         */
        public T create(boolean useGPU);
    }
    
    /** A queue that prioritizes if {@code hasPriority==true} and blocks if {@link PriorityBlockingQueue#take} is called but no elements are available. */
    private PriorityBlockingQueue<WithPriority<T>> queue;
    
    public ConcurrentModelPool(ConcurrencyPlan plan, CreateModelForPool<T> createModel) {
        queue = new PriorityBlockingQueue<>();
        
        addNumberModels( plan.numberGPUs(), true, createModel);
        addNumberModels( plan.totalMinusGPUs(), false, createModel);
    }
    
    /**
     * Execute on the next available model (or wait until one becomes available)
     *
     * @param functionToExecute function to execute on a given model
     * @param <S> return type 
     * @throws InterruptedException
     */
    public <S> S excuteOrWait( Function<T,S> functionToExecute ) throws InterruptedException {
        WithPriority<T> model = getOrWait();
        try {
            return functionToExecute.apply(model.get());
        } finally {
            giveBack(model);
        }
    }
    
    /**
     * Gets an instantiated model to be used.
     * 
     * <p>After usage, {@link #giveBack} should be called to return the model to the pool.
     * 
     * @return
     * @throws InterruptedException 
     */
    private WithPriority<T> getOrWait() throws InterruptedException {
        return queue.take();
    }
        
    /**
     * Returns the model to the pool.
     * 
     * @param model the model to return
     */
    private void giveBack(WithPriority<T> model) {
        queue.put(model);
    }
        
    private void addNumberModels( int numberModels, boolean useGPU, CreateModelForPool<T> createModel ) {
        IntStream.range(0, numberModels).forEach( number ->
            queue.add( create(useGPU, createModel) )
        );
    }
    
    private WithPriority<T> create(boolean useGPU, CreateModelForPool<T> createModel) {
        return new WithPriority<>(createModel.create(useGPU), useGPU);
    }
}