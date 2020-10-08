/*-
 * #%L
 * anchor-experiment
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

package org.anchoranalysis.experiment.bean.processor;

import com.google.common.base.Preconditions;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.TaskStatistics;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.outputter.Outputter;

/**
 * Executes different processors depending on whether we are in debug mode or not
 *
 * @author Owen Feehan
 * @param <T> input-object type
 * @param <S> shared-object state
 */
public class DebugDependentProcessor<T extends InputFromManager, S> extends JobProcessor<T, S> {

    // START BEAN PROPERTIES
    /**
     * An upper limit on the number of the processors that can be simultaneously used in parallel,
     * if they are available.
     */
    @BeanField @Getter @Setter private int maxNumberProcessors;

    /**
     * How many processors to avoid using for the tasks.
     *
     * <p>When using the maximum available number of processors, a certain amount are deliberately
     * not used, to save them for other tasks on the operating system. This is particularly valuable
     * on a desktop PC where other tasks (e.g web browsing) may be ongoing during processing.
     */
    @BeanField @Getter @Setter private int keepProcessorsFree = 1;
    // END BEAN PROPERTIES

    @Override
    protected TaskStatistics execute(
            Outputter rootOutputter, List<T> inputs, ParametersExperiment paramsExperiment)
            throws ExperimentExecutionException {

        Preconditions.checkArgument(rootOutputter.getChecked().getSettings().hasBeenInit());

        JobProcessor<T, S> processor =
                createProcessor(paramsExperiment.getExperimentArguments().isDebugModeEnabled());
        return processor.execute(rootOutputter, inputs, paramsExperiment);
    }

    private JobProcessor<T, S> createProcessor(boolean debugMode) {
        if (debugMode) {
            return createSequentialProcessor();
        } else {
            return creareParallelProcessor();
        }
    }

    private SequentialProcessor<T, S> createSequentialProcessor() {
        SequentialProcessor<T, S> processor = new SequentialProcessor<>();
        processor.setTask(getTask());
        processor.setSuppressExceptions(isSuppressExceptions());
        return processor;
    }

    private ParallelProcessor<T, S> creareParallelProcessor() {
        ParallelProcessor<T, S> processor = new ParallelProcessor<>();
        processor.setMaxNumberProcessors(maxNumberProcessors);
        processor.setTask(getTask());
        processor.setSuppressExceptions(isSuppressExceptions());
        processor.setKeepProcessorsFree(keepProcessorsFree);
        return processor;
    }
}
