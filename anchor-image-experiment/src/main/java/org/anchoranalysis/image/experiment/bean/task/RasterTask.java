package org.anchoranalysis.image.experiment.bean.task;

/*
 * #%L
 * anchor-image-experiment
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.bean.task.TaskWithoutSharedState;
import org.anchoranalysis.experiment.task.InputTypesExpected;
import org.anchoranalysis.experiment.task.NoSharedState;
import org.anchoranalysis.experiment.task.InputBound;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.input.NamedChnlsInput;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.bound.BoundIOContext;

/**
 * An experiment that primarily takes a raster image as an input
 * 
 * No shared-state is allowed
 * 
 * @author Owen Feehan
 *
 */
public abstract class RasterTask extends TaskWithoutSharedState<NamedChnlsInput> {

	// Raster experiment
	public RasterTask() {
	}
	
	@Override
	public InputTypesExpected inputTypesExpected() {
		return new InputTypesExpected(NamedChnlsInput.class);
	}

	@Override
	public void doJobOnInputObject( InputBound<NamedChnlsInput,NoSharedState> params ) throws JobExecutionException {

		NamedChnlsInput inputObject = params.getInputObject();
		BoundOutputManagerRouteErrors outputManager = params.getOutputManager();
		
		try	{
			int numSeries = inputObject.numSeries();
			
			startSeries(
				outputManager,
				params.getLogger().getErrorReporter()
			);
			
			for (int s=0; s<numSeries; s++) {
				doStack(
					inputObject,
					s,
					numSeries,
					params.context()
				);
			}
			
			endSeries(outputManager);
			
		} catch (RasterIOException e) {
			throw new JobExecutionException(e);
		}
	}
	
	public abstract void startSeries( BoundOutputManagerRouteErrors outputManager, ErrorReporter errorReporter ) throws JobExecutionException;
	
	/**
	 * Processes one stack from a series
	 * 
	 * @param inputObject the input-object corresponding to this stack (a set of named-channels)
	 * @param seriesIndex the index that is being currently processed from the series
	 * @param numSeries the total number of images in the series (constant for a given task)
	 * @param context TODO
	 * @throws JobExecutionException
	 */
	public abstract void doStack( NamedChnlsInput inputObject, int seriesIndex, int numSeries, BoundIOContext context ) throws JobExecutionException;
	
	public abstract void endSeries(BoundOutputManagerRouteErrors outputManager) throws JobExecutionException;
}
