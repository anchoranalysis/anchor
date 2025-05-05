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
