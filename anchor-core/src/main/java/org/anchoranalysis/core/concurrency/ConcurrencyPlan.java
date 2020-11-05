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
 * How many allocated CPUS and CPUs can be used.
 *
 * @author Owen Feehan
 */
public class ConcurrencyPlan {

    // GPUs are currently disabled, as no linked library can benefit from using them.
    public static final int DEFAULT_NUMBER_GPUS = 0;
    
    /** The total number of available CPUs processors */
    private final int totalNumberCPUs;

    /** The maximum number of available GPUs that can be used (to substitute for a CPU at places) */
    private final int numberGPUs;

    /**
     * Creates a plan for a single-CPU processor with the default number of GPUs.
     *
     * @return
     */
    public static ConcurrencyPlan singleProcessor() {
        return singleProcessor(DEFAULT_NUMBER_GPUS);
    }
    
    /**
     * Creates a plan for a single-CPU processor - with a maximum number of GPUs.
     *
     * @return
     */
    public static ConcurrencyPlan singleProcessor(int maxNumberGPUs) {
        return new ConcurrencyPlan(1, maxNumberGPUs);
    }

    /**
     * Creates a plan for a multiple CPU-processors
     *
     * @return
     */
    public static ConcurrencyPlan multipleProcessors(int maxNumberCPUs, int maxNumberGPUs) {
        return new ConcurrencyPlan(maxNumberCPUs, maxNumberGPUs);
    }

    public int totalNumber() {
        return totalNumberCPUs;
    }

    public int numberGPUs() {
        return numberGPUs;
    }

    public int totalMinusGPUs() {
        return totalNumberCPUs - numberGPUs;
    }

    private ConcurrencyPlan(int maxAvailableCPUs, int maxNumberGPUs) {
        this.totalNumberCPUs = Math.max(maxAvailableCPUs, maxNumberGPUs);
        this.numberGPUs = Math.min(maxNumberGPUs, maxAvailableCPUs);
    }
}
