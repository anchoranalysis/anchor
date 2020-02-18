package org.anchoranalysis.experiment.bean.processor;

/*-
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.util.List;

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
 *
 * @param <T> input-object type
 * @param <S> shared-object state
 */
public class DebugDependentProcessor<T extends InputFromManager,S> extends JobProcessor<T,S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private int maxNumProcessors;
	
	@BeanField
	private boolean supressExceptions = false;
	// END BEAN PROPERTIES

	@Override
	protected TaskStatistics execute(	BoundOutputManagerRouteErrors rootOutputManager, List<T> inputObjects, ParametersExperiment paramsExperiment )
			throws ExperimentExecutionException {
		
		assert( rootOutputManager.getDelegate().getOutputWriteSettings().hasBeenInit());
		
		JobProcessor<T,S> processor = createProcessor( paramsExperiment.getExperimentArguments().isDebugEnabled() );
		return processor.execute(rootOutputManager, inputObjects, paramsExperiment);
	}
	
	private JobProcessor<T,S> createProcessor( boolean debugMode ) {
		if (debugMode) {
			SequentialProcessor<T,S> sp = new SequentialProcessor<>();
			sp.setTask(getTask());
			sp.setSupressExceptions(supressExceptions);
			return sp;
		} else {
			ParallelProcessor<T,S> pp = new ParallelProcessor<>();
			pp.setMaxNumProcessors(maxNumProcessors);
			pp.setTask(getTask());
			pp.setSupressExceptions(supressExceptions);
			return pp;
		}
	}

	public int getMaxNumProcessors() {
		return maxNumProcessors;
	}

	public void setMaxNumProcessors(int maxNumProcessors) {
		this.maxNumProcessors = maxNumProcessors;
	}

	public boolean isSupressExceptions() {
		return supressExceptions;
	}

	public void setSupressExceptions(boolean supressExceptions) {
		this.supressExceptions = supressExceptions;
	}
}
