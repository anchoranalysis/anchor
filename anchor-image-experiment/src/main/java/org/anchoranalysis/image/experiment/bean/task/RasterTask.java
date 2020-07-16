/* (C)2020 */
package org.anchoranalysis.image.experiment.bean.task;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.bean.task.TaskWithoutSharedState;
import org.anchoranalysis.experiment.task.InputBound;
import org.anchoranalysis.experiment.task.InputTypesExpected;
import org.anchoranalysis.experiment.task.NoSharedState;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.input.NamedChnlsInput;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

/**
 * An experiment that primarily takes a raster image as an input
 *
 * <p>No shared-state is allowed
 *
 * @author Owen Feehan
 */
public abstract class RasterTask extends TaskWithoutSharedState<NamedChnlsInput> {

    @Override
    public InputTypesExpected inputTypesExpected() {
        return new InputTypesExpected(NamedChnlsInput.class);
    }

    @Override
    public void doJobOnInputObject(InputBound<NamedChnlsInput, NoSharedState> params)
            throws JobExecutionException {

        NamedChnlsInput inputObject = params.getInputObject();
        BoundOutputManagerRouteErrors outputManager = params.getOutputManager();

        try {
            int numSeries = inputObject.numSeries();

            startSeries(outputManager, params.getLogger().errorReporter());

            for (int s = 0; s < numSeries; s++) {
                doStack(inputObject, s, numSeries, params.context());
            }

            endSeries(outputManager);

        } catch (RasterIOException e) {
            throw new JobExecutionException(e);
        }
    }

    public abstract void startSeries(
            BoundOutputManagerRouteErrors outputManager, ErrorReporter errorReporter)
            throws JobExecutionException;

    /**
     * Processes one stack from a series
     *
     * @param inputObject the input-object corresponding to this stack (a set of named-channels)
     * @param seriesIndex the index that is being currently processed from the series
     * @param numSeries the total number of images in the series (constant for a given task)
     * @param context IO context
     * @throws JobExecutionException
     */
    public abstract void doStack(
            NamedChnlsInput inputObject, int seriesIndex, int numSeries, BoundIOContext context)
            throws JobExecutionException;

    public abstract void endSeries(BoundOutputManagerRouteErrors outputManager)
            throws JobExecutionException;
}
