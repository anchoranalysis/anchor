/*-
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2010 - 2025 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.experiment.bean.processor;

import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.io.input.InputFromManager;
import org.junit.jupiter.api.Test;

/** Base class for processor tests. */
abstract class ProcessorTestBase {

    @SuppressWarnings("javadoc")
    @Test
    void testExecuteJobsInParallel() throws ExperimentExecutionException, JobExecutionException {

        JobProcessor<InputFromManager, Object> processor = createProcessor();

        ExecuteHelper.assertExecutionTime(processor, minExecutionTimeMillis());
    }

    /**
     * Minimum expected execution time in milliseconds.
     *
     * @return the minimum expected execution time.
     */
    protected abstract int minExecutionTimeMillis();

    /**
     * Create the processor to be tested.
     *
     * @return the created processor.
     */
    protected abstract JobProcessor<InputFromManager, Object> createProcessor();
}
