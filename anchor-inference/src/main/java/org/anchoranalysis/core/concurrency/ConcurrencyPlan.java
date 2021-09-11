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

/**
 * How many allocated CPUs and CPUs can be used concurrently for inference.
 *
 * @author Owen Feehan
 */
public class ConcurrencyPlan {

    /** The default number of GPUs used for {@code numberGPUs} in certain constructors. */
    public static final int DEFAULT_NUMBER_GPUS = 1;

    /** The maximum number of CPU processors that may be used, if available. */
    private final int numberCPUs;

    /** The maximum number of GPU processors that may be used, if available. */
    private final int numberGPUs;

    /**
     * Creates a plan for no CPU processors with the default number of GPUs.
     *
     * @return a newly-created plan
     */
    public static ConcurrencyPlan noCPUProcessor() {
        return new ConcurrencyPlan(0, DEFAULT_NUMBER_GPUS);
    }

    /**
     * Creates a plan for a single-CPU processor with the default number of GPUs.
     *
     * @return a newly-created plan
     */
    public static ConcurrencyPlan singleCPUProcessor() {
        return singleCPUProcessor(DEFAULT_NUMBER_GPUS);
    }

    /**
     * Creates a plan for a single-CPU processor - with a maximum number of GPUs.
     *
     * @param maxNumberGPUs the maximum number of available GPUs that may be used, if available.
     * @return a newly-created plan
     */
    public static ConcurrencyPlan singleCPUProcessor(int maxNumberGPUs) {
        return new ConcurrencyPlan(1, maxNumberGPUs);
    }

    /**
     * Creates a plan for a multiple CPU-processors.
     *
     * @param maxNumberCPUs the maximum number of CPU processors that may be used, if available.
     * @param maxNumberGPUs the maximum number of GPU processors that may be used, if available.
     * @return a newly-created plan
     */
    public static ConcurrencyPlan multipleProcessors(int maxNumberCPUs, int maxNumberGPUs) {
        return new ConcurrencyPlan(maxNumberCPUs, maxNumberGPUs);
    }

    /**
     * Derive a {@link ConcurrencyPlan} that preserves the number of CPUs but disables all GPUs.
     *
     * <p>This operation is immutable.
     *
     * @return a newly recreated {@link ConcurrencyPlan}
     */
    public ConcurrencyPlan disableGPUs() {
        return new ConcurrencyPlan(numberCPUs, 0);
    }

    /**
     * The number of CPUs to be used in the plan.
     *
     * @return the number of CPUs
     */
    public int numberCPUs() {
        return numberCPUs;
    }

    /**
     * The number of GPUs to be used in the plan.
     *
     * @return the number of GPUs
     */
    public int numberGPUs() {
        return numberGPUs;
    }

    /**
     * Creates a plan, ensuring there there are never more GPUs than CPUs.
     *
     * @param numberCPUs the maximum number of CPU processors that may be used, if available.
     * @param numberGPUs the maximum number of GPU processors that may be used, if available.
     */
    private ConcurrencyPlan(int numberCPUs, int numberGPUs) {
        this.numberCPUs = Math.max(numberCPUs, numberGPUs);
        this.numberGPUs = Math.min(numberGPUs, numberCPUs);
    }
}
