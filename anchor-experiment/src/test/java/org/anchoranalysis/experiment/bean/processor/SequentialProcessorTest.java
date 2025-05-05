package org.anchoranalysis.experiment.bean.processor;

import org.anchoranalysis.io.input.InputFromManager;

/** Tests the {@link SequentialProcessor} class. */
class SequentialProcessorTest extends ProcessorTestBase {

    @Override
    protected int minExecutionTimeMillis() {
        return ExecuteHelper.TASK_DELAY_MS * ExecuteHelper.NUMBER_OF_INPUTS;
    }

    @Override
    protected JobProcessor<InputFromManager, Object> createProcessor() {
        return new SequentialProcessor<>();
    }
}
