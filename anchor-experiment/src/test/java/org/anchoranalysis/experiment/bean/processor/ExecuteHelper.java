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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.bean.task.Task;
import org.anchoranalysis.experiment.log.StatefulMessageLogger;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.outputter.Outputter;

/** Helper class to run inputs on a processor and make assertions. */
class ExecuteHelper {

    /** How long to delay each task in milliseconds to simulate work. */
    public static final int TASK_DELAY_MS = 50;

    /** Number of inputs to test with. */
    public static final int NUMBER_OF_INPUTS = 10;

    /***
     * Executes each input on the processor and asserts the execution time lies above a threshold.
     *
     * @param processor the processor to execute inputs with.
     * @param minExpectedExecutionTime a minimum expected execution time.
     * @throws JobExecutionException if thrown during job execution.
     * @throws ExperimentExecutionException if thrown during job execution.
     */
    public static void assertExecutionTime(
            JobProcessor<InputFromManager, Object> processor, int minExpectedExecutionTime)
            throws JobExecutionException, ExperimentExecutionException {

        List<InputFromManager> inputs =
                MockInputFixture.createInputs(ExecuteHelper.NUMBER_OF_INPUTS);

        long executionTime = executeInputsOnProcessor(processor, inputs);

        assertTrue(
                executionTime >= minExpectedExecutionTime,
                "Execution time should be more than %d ms, but is %d ms."
                        .formatted(minExpectedExecutionTime, executionTime));
    }

    /**
     * Execute each input on the processor and returns the total execution time.
     *
     * @param processor the processor to execute inputs with.
     * @param inputs the inputs to test with.
     * @throws JobExecutionException if thrown during job execution.
     * @throws ExperimentExecutionException if thrown during job execution.
     * @return the total execution time in milliseconds.
     */
    public static long executeInputsOnProcessor(
            JobProcessor<InputFromManager, Object> processor, List<InputFromManager> inputs)
            throws JobExecutionException, ExperimentExecutionException {
        ParametersExperiment mockParameters = createParametersExperiment();
        Outputter mockOutputter = mock(Outputter.class);

        // Mock the getTask() method to return a mock Task
        Task<InputFromManager, Object> task = createTask();
        processor.setTask(task);

        long startTime = System.currentTimeMillis();
        processor.execute(mockOutputter, inputs, mockParameters);
        long endTime = System.currentTimeMillis();

        verify(task, times(NUMBER_OF_INPUTS)).executeJob(any());

        return endTime - startTime;
    }

    private static Task<InputFromManager, Object> createTask() throws JobExecutionException {
        @SuppressWarnings("unchecked")
        Task<InputFromManager, Object> task = mock(Task.class);
        when(task.duplicateBean()).thenReturn(task);
        doAnswer(
                        invocation -> {
                            Thread.sleep(TASK_DELAY_MS); // Simulate some work
                            return null;
                        })
                .when(task)
                .executeJob(any());
        return task;
    }

    private static ParametersExperiment createParametersExperiment() {
        StatefulMessageLogger logger = mock(StatefulMessageLogger.class);

        ParametersExperiment parameters = mock(ParametersExperiment.class);
        when(parameters.getLoggerExperiment()).thenReturn(logger);
        return parameters;
    }
}
