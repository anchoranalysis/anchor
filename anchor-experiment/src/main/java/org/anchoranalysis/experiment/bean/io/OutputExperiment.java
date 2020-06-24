package org.anchoranalysis.experiment.bean.io;

import java.util.Optional;

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


import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.error.reporter.ErrorReporterIntoLog;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.Experiment;
import org.anchoranalysis.experiment.bean.identifier.ExperimentIdentifier;
import org.anchoranalysis.experiment.bean.logreporter.ConsoleLogReporterBean;
import org.anchoranalysis.experiment.bean.logreporter.LogReporterBean;
import org.anchoranalysis.experiment.log.ConsoleLogReporter;
import org.anchoranalysis.experiment.log.reporter.StatefulLogReporter;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.error.FilePathPrefixerException;
import org.anchoranalysis.io.generator.text.StringGenerator;
import org.anchoranalysis.io.generator.xml.XMLConfigurationWrapperGenerator;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.bound.BindFailedException;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.apache.commons.lang.time.StopWatch;

public abstract class OutputExperiment extends Experiment {

	// START BEAN PROPERTIES
	@BeanField
	private OutputManager output = null;
	
	@BeanField
	private String outputNameConfigCopy = "config";
	
	@BeanField
	private String outputNameManifestExperiment = "manifestExperiment";
	
	@BeanField
	private String outputNameExecutionTime = "executionTime";
	
	@BeanField
	private LogReporterBean logReporterExperiment = new ConsoleLogReporterBean();
	
	@BeanField
	private ExperimentIdentifier experimentIdentifier = null;
	
	/**
	 * if TRUE, then detailed messages around each experiment (name, time, start-stop events etc.) are ALWAYS displayed
	 * if FALSE, these will sometimes be hidden (e.g. if the execution of each file is very quick) 
	 */
	@BeanField
	private boolean forceDetailedLogging = false;
	// END BEAN PROPERTIES
	
	// Runs the experiment on all files
	public void doExperiment(ExperimentExecutionArguments expArgs) throws ExperimentExecutionException {

		try {
			doExperimentWithParams(
				createParams(expArgs)
			);
			
		} catch (AnchorIOException e) {
			throw new ExperimentExecutionException(e);
		}
	}
	
	private void doExperimentWithParams( ParametersExperiment params ) throws ExperimentExecutionException {
		try {
			StopWatch stopWatchExperiment = new StopWatch();
			stopWatchExperiment.start();
			
			
			initBeforeDo( params.getOutputManager(), params.getExperimentArguments().isDebugEnabled() );
			
			params.getLogReporterExperiment().start( );
					
			if (params.isDetailedLogging()) {
				params.getLogReporterExperiment().logFormatted(
					"Experiment %s started writing to %s",
					params.getExperimentIdentifier(),
					params.getOutputManager().getOutputFolderPath()
				);
			}
			
			writeConfigCopy(params.getOutputManager());

			assert( params.getOutputManager().getDelegate().getOutputWriteSettings().hasBeenInit());
			
			execExperiment( params );
			
			writeExecutionTime(params.getOutputManager(), stopWatchExperiment);
			
			// Outputs after processing
			stopWatchExperiment.stop();
			
			if (params.isDetailedLogging()) {
				params.getLogReporterExperiment().logFormatted( "Experiment %s completed (%ds) writing to %s",
					params.getExperimentIdentifier(),
					stopWatchExperiment.getTime() / 1000,
					params.getOutputManager().getOutputFolderPath()
				);
			}
			
		} finally {
			
			// An experiment is considered always successful
			params.getLogReporterExperiment().close(true);
		}
	}

	protected abstract void execExperiment( ParametersExperiment params ) throws ExperimentExecutionException;
	
	private ParametersExperiment createParams(ExperimentExecutionArguments expArgs) throws AnchorIOException {
		
		ManifestRecorder experimentalManifest = new ManifestRecorder();
		
		String experimentId = experimentIdentifier.identifier( expArgs.getTaskName() );
		
		try {
			BoundOutputManager rootOutputManager = 
				getOutput().bindRootFolder( experimentId, experimentalManifest, expArgs.createParamsContext() );
			
			assert( rootOutputManager.getOutputWriteSettings().hasBeenInit() );
			
			StatefulLogReporter logReporter = createExperimentLog(rootOutputManager, expArgs, useDetailedLogging());
	
			// Important we bind to a root folder before any log messages go out, as certain log
			//  appenders require the OutputManager to be set before outputting to the correct location
			//  and this only occurs after the call to bindRootFolder()
			return new ParametersExperiment(
				expArgs,
				experimentId,
				Optional.of(experimentalManifest),
				rootOutputManager,
				logReporter,
				new ErrorReporterIntoLog(logReporter),
				useDetailedLogging()
			);
		} catch (FilePathPrefixerException e) {
			throw new AnchorIOException("Cannot create params-context", e);
		} catch (BindFailedException e) {
			throw new AnchorIOException("Bind failed", e);
		}
	}
	
	private void initBeforeDo( BoundOutputManagerRouteErrors bom, boolean debugMode ) {
		UpdateLog4JOutputManager.updateLog4J(bom);
	}
	
	private StatefulLogReporter createExperimentLog(
		BoundOutputManager rootOutputManagerNoErrors,
		ExperimentExecutionArguments expArgs,
		boolean detailedLogging
	) {
		ErrorReporter errorReporterFallback = new ErrorReporterIntoLog( new ConsoleLogReporter() );
		
		return logReporterExperiment.create(
			"experiment_log",
			rootOutputManagerNoErrors,
			errorReporterFallback,
			expArgs,
			detailedLogging
		);
	}
	
	/** Maybe writes a copy of a configuration */
	private void writeConfigCopy(BoundOutputManagerRouteErrors rootOutputManager) {
		rootOutputManager.getWriterCheckIfAllowed().write(
			outputNameConfigCopy,
			() -> new XMLConfigurationWrapperGenerator( getXMLConfiguration() )
		);
	}
	
	/** Maybe writes the execution time to the file-system */
	private void writeExecutionTime(BoundOutputManagerRouteErrors rootOutputManager, StopWatch stopWatchExperiment) {
		rootOutputManager.getWriterCheckIfAllowed().write(
			outputNameExecutionTime,
			() -> new StringGenerator( Long.toString(stopWatchExperiment.getTime()) )
		);
	}
		
	@Override
	public boolean useDetailedLogging() {
		return forceDetailedLogging;
	}

	public boolean isForceDetailedLogging() {
		return forceDetailedLogging;
	}
	
	public String getOutputNameConfigCopy() {
		return outputNameConfigCopy;
	}


	public void setOutputNameConfigCopy(String outputNameConfigCopy) {
		this.outputNameConfigCopy = outputNameConfigCopy;
	}
	
	
	public OutputManager getOutput() {
		return output;
	}


	public void setOutput(OutputManager output) {
		this.output = output;
	}
	
	public LogReporterBean getLogReporterExperiment() {
		return logReporterExperiment;
	}


	public void setLogReporterExperiment(LogReporterBean logReporter) {
		this.logReporterExperiment = logReporter;
	}


	public ExperimentIdentifier getExperimentIdentifier() {
		return experimentIdentifier;
	}


	public void setExperimentIdentifier(ExperimentIdentifier experimentIdentifier) {
		this.experimentIdentifier = experimentIdentifier;
	}


	public void setForceDetailedLogging(boolean forceDetailedLogging) {
		this.forceDetailedLogging = forceDetailedLogging;
	}


}
