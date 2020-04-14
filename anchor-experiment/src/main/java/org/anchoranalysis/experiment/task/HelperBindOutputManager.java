package org.anchoranalysis.experiment.task;

/*
 * #%L
 * anchor-experiment
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


import java.util.Optional;

import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;


class HelperBindOutputManager {
	
	// If pathForBinding is null, we bind to the root folder instead
	public static BoundOutputManager createOutputManagerForTask(
		InputFromManager input,
		Optional<ManifestRecorder> manifestTask,
		ParametersExperiment params
	) throws JobExecutionException {
		try {
			if (input.pathForBinding()!=null) {
				return createWithBindingPath(input, manifestTask, params);
			} else {
				return createWithoutBindingPath(manifestTask, params.getOutputManager() );
			}
		} catch (AnchorIOException e) {
			throw new JobExecutionException(
				String.format(
					"Cannot bind the outputManager to the specific task with pathForBinding=%s and experimentIdentifier='%s'%n%s",
					describeInputForBinding(input),
					params.getExperimentIdentifier(),
					e.toString()
				),
				e
			);
		}
	}

	private static BoundOutputManager createWithBindingPath(
		InputFromManager input,
		Optional<ManifestRecorder> manifestTask,
		ParametersExperiment params
	) throws AnchorIOException, JobExecutionException {
		BoundOutputManager boundOutput = params.getOutputManager().bindFile( 
			input,
			params.getExperimentIdentifier(),
			manifestTask,
			params.getExperimentalManifest(),
			params.getExperimentArguments().createParamsContext()
		);
		if (params.getExperimentalManifest().isPresent()) {
			ManifestClashChecker.throwExceptionIfClashes(
				params.getExperimentalManifest().get(),
				boundOutput,
				input.pathForBinding()
			);
		}
		return boundOutput;
	}
			
	private static BoundOutputManager createWithoutBindingPath( Optional<ManifestRecorder> manifestTask, BoundOutputManagerRouteErrors outputManager ) {
		manifestTask.ifPresent( mt-> {
			mt.init( outputManager.getOutputFolderPath() );
			outputManager.addOperationRecorder( mt.getRootFolder() );
		});
		return outputManager.getDelegate();
	}

	private static String describeInputForBinding( InputFromManager input ) {
		if (input.pathForBinding()!=null) {
			return quoteString(input.pathForBinding().toString());
		} else {
			return "null";
		}
	}
	
	private static String quoteString( String input ) {
		return String.format("'%s'", input);
	}
}
