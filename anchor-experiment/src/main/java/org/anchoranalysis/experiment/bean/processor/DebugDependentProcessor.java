/* (C)2020 */
package org.anchoranalysis.experiment.bean.processor;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.TaskStatistics;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

/**
 * Executes different processors depending on whether we are in debug mode or not
 *
 * @author Owen Feehan
 * @param <T> input-object type
 * @param <S> shared-object state
 */
public class DebugDependentProcessor<T extends InputFromManager, S> extends JobProcessor<T, S> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private int maxNumProcessors;

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
            BoundOutputManagerRouteErrors rootOutputManager,
            List<T> inputObjects,
            ParametersExperiment paramsExperiment)
            throws ExperimentExecutionException {

        assert (rootOutputManager.getDelegate().getOutputWriteSettings().hasBeenInit());

        JobProcessor<T, S> processor =
                createProcessor(paramsExperiment.getExperimentArguments().isDebugModeEnabled());
        return processor.execute(rootOutputManager, inputObjects, paramsExperiment);
    }

    private JobProcessor<T, S> createProcessor(boolean debugMode) {
        if (debugMode) {
            SequentialProcessor<T, S> sp = new SequentialProcessor<>();
            sp.setTask(getTask());
            sp.setSuppressExceptions(isSuppressExceptions());
            return sp;
        } else {
            ParallelProcessor<T, S> pp = new ParallelProcessor<>();
            pp.setMaxNumProcessors(maxNumProcessors);
            pp.setTask(getTask());
            pp.setSuppressExceptions(isSuppressExceptions());
            pp.setKeepProcessorsFree(keepProcessorsFree);
            return pp;
        }
    }
}
