package org.anchoranalysis.experiment.bean.processor;

import org.anchoranalysis.io.input.InputFromManager;

/** Tests the {@link ParallelProcessor} class. */
class ParallelProcessorTest extends ProcessorTestBase {

    @Override
    protected JobProcessor<InputFromManager, Object> createProcessor() {
        ParallelProcessor<InputFromManager, Object> processor = new ParallelProcessor<>();
        processor.setMaxNumberProcessors(4);
        processor.setKeepProcessorsFree(0);
        return processor;
    }

    @Override
    protected int minExecutionTimeMillis() {
        return ExecuteHelper.TASK_DELAY_MS;
    }
}
