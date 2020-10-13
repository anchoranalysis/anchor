/*-
 * #%L
 * anchor-image-experiment
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

package org.anchoranalysis.image.experiment.bean.task;

import org.anchoranalysis.core.functional.FunctionalIterate;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.bean.task.Task;
import org.anchoranalysis.experiment.task.InputBound;
import org.anchoranalysis.experiment.task.InputTypesExpected;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.input.NamedChannelsInput;
import org.anchoranalysis.io.output.outputter.InputOutputContext;

/**
 * An experiment that takes (primarily) a series of raster images as an input.
 *
 * <p>An operation is executed independently on each stack in the series.
 *
 * @param <S> shared-state type for entire task
 * @param <U> shared-state type for current series i.e. for all stacks in one particular job
 * @author Owen Feehan
 */
public abstract class RasterTask<S,U> extends Task<NamedChannelsInput,S> {

    @Override
    public void doJobOnInput(InputBound<NamedChannelsInput, S> input)
            throws JobExecutionException {

        InputOutputContext context = input.context();

        try {
            int numberSeries = input.getInput().numberSeries();

            U sharedStateJob = createSharedStateJob(context);
            
            startSeries(input.getSharedState(), sharedStateJob, context);

            FunctionalIterate.repeatWithIndex(numberSeries, seriesIndex ->
                doStack(input, sharedStateJob, seriesIndex, numberSeries, context)
            );

            endSeries(input.getSharedState(), sharedStateJob, context);

        } catch (ImageIOException e) {
            throw new JobExecutionException(e);
        }
    }
    
    /**
     * Creates a shared-state for the duration of a particular input-job
     * 
     * <p>This will exist across all stacks from the same series.
     * 
     * @param context the input-output cotnext associated with a particular job
     * @return a newly created shared-state
     * @throws JobExecutionException
     */
    protected abstract U createSharedStateJob(InputOutputContext context) throws JobExecutionException;

    /**
     * Starts processing of a series.
     * 
     * <p>This corresponds to the start of an input job, <b>before</b> any stacks in the series are processed.
     * 
     * <p>This should be called always <i>once</i> <b>before</b> all calls to {@link #doStack}.
     * @param sharedStateTask shared-state across all jobs in task
     * @param sharedStateJob shared-state across all stacks in a job (i.e. in all series.)
     * @param context input-output context
     * @throws JobExecutionException
     */
    public abstract void startSeries(S sharedStateTask, U sharedStateJob, InputOutputContext context)
            throws JobExecutionException;

    /**
     * Processes one stack from a series.
     * 
     * <p>This can be called many times in a job, once for each stack in the series.
     * 
     * <p>It is assumed each job may have only one series.
     *
     * @param input the input-object corresponding to this stack (a set of named-channels)
     * @param sharedStateJob shared-state across all stacks in a job (i.e. in all series.)
     * @param seriesIndex the index of the input that is being currently processed from the series.
     * @param numberSeries the total number of images in the series (constant for a given task)
     * @param context IO context
     * @throws JobExecutionException
     */
    public abstract void doStack(
            InputBound<NamedChannelsInput, S> input, U sharedStateJob, int seriesIndex, int numberSeries, InputOutputContext context)
            throws JobExecutionException;

    /**
     * Ends processing of a series.
     * 
     * <p>This corresponds to the end of an input job, <b>after</b> any stacks in the series are processed.
     * 
     * <p>This should be called always <i>once</i> <b>after</b> all calls to {@link #doStack}.
     * @param sharedStateTask shared-state across all jobs in task
     * @param sharedStateJob shared-state across all stacks in a job (i.e. in all series.)
     * @param context input-output context
     * @throws JobExecutionException
     */
    public abstract void endSeries(S sharedStateTask, U sharedStateJob, InputOutputContext context) throws JobExecutionException;

    @Override
    public InputTypesExpected inputTypesExpected() {
        return new InputTypesExpected(NamedChannelsInput.class);
    }
}
