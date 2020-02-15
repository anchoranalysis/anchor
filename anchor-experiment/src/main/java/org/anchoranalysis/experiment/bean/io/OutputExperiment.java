package org.anchoranalysis.experiment.bean.io;

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


import java.io.IOException;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.error.reporter.ErrorReporterIntoLog;
import org.anchoranalysis.core.log.LogReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.Experiment;
import org.anchoranalysis.experiment.bean.identifier.ExperimentIdentifier;
import org.anchoranalysis.experiment.bean.logreporter.ConsoleLogReporterBean;
import org.anchoranalysis.experiment.bean.logreporter.LogReporterBean;
import org.anchoranalysis.experiment.log.ConsoleLogReporter;
import org.anchoranalysis.experiment.log.reporter.StatefulLogReporter;
import org.anchoranalysis.io.generator.serialized.ObjectOutputStreamGenerator;
import org.anchoranalysis.io.generator.serialized.XStreamGenerator;
import org.anchoranalysis.io.generator.text.StringGenerator;
import org.anchoranalysis.io.generator.xml.XMLConfigurationWrapperGenerator;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.apache.commons.lang.time.StopWatch;

public abstract class OutputExperiment extends Experiment {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3175946512104697326L;

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
	private LogReporterBean logReporterBean = new ConsoleLogReporterBean();
	
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
			StopWatch stopWatchExperiment = new StopWatch();
			stopWatchExperiment.start();
			
			ManifestRecorder experimentalManifest = new ManifestRecorder();
			
			getExperimentIdentifier().init( expArgs.isGUIEnabled() );
			
			assert( getOutput().getOutputWriteSettings().hasBeenInit() );
			
			BoundOutputManager rootOutputManagerNoErrors = 
				getOutput().bindRootFolder( this.getExperimentIdentifier().identifier(), experimentalManifest, expArgs.isDebugEnabled() );
			
			assert( rootOutputManagerNoErrors.getOutputWriteSettings().hasBeenInit() );
			
			// To reporter errors when trying to do logging
			ErrorReporter errorReporterFallback = new ErrorReporterIntoLog( new ConsoleLogReporter() );
			
			StatefulLogReporter logReporter = logReporterBean.create( rootOutputManagerNoErrors, errorReporterFallback, expArgs);
			ErrorReporter errorReporter = new ErrorReporterIntoLog( logReporter );
			
			
			// Important we bind to a root folder before any log messages go out, as certain log
			//  appenders require the OutputManager to be set before outputting to the correct location
			//  and this only occurs after the call to bindRootFolder()
			BoundOutputManagerRouteErrors rootOutputManager = new BoundOutputManagerRouteErrors(
				rootOutputManagerNoErrors,
				errorReporter
			);
			
			try {
				
				initBeforeDo( rootOutputManager, expArgs.isDebugEnabled() );
				
				logReporter.start( );
			
				rootOutputManager.getWriterCheckIfAllowed().write(
					outputNameConfigCopy,
					() -> new XMLConfigurationWrapperGenerator( getXMLConfiguration() )
				);

				boolean detailedLogging = useDetailedLogging();
				
				if (detailedLogging) {
					logReporter.logFormatted(
						"Experiment %s started writing to %s",
						getExperimentIdentifier().identifier(),
						rootOutputManager.getOutputFolderPath()
					);
				}
	
				assert( rootOutputManager.getDelegate().getOutputWriteSettings().hasBeenInit());
				execExperiment( rootOutputManager, experimentalManifest, expArgs, logReporter );
				
				rootOutputManager.getWriterCheckIfAllowed().write(
					outputNameManifestExperiment,
					() -> new XStreamGenerator<Object>( experimentalManifest, "ManifestRecorder"  )
				);
				rootOutputManager.getWriterCheckIfAllowed().write(
					outputNameManifestExperiment,
					() -> new ObjectOutputStreamGenerator<>( experimentalManifest, "ManifestRecorder" )
				);
				rootOutputManager.getWriterCheckIfAllowed().write(
					outputNameExecutionTime,
					() -> new StringGenerator( Long.toString(stopWatchExperiment.getTime()) )
				);
				
				// Outputs after processing
				stopWatchExperiment.stop();
				
				if (detailedLogging) {
					logReporter.logFormatted( "Experiment %s completed (%ds)", getExperimentIdentifier().identifier(), stopWatchExperiment.getTime() / 1000);
				}

				// Report here on how many successful tasks ocurred
				
			} finally {
				
				// An experiment is considered always successful
				logReporter.close(true);
			}
			
		} catch (IOException e) {
			throw new ExperimentExecutionException(e);
		}
	}

	protected abstract void execExperiment( BoundOutputManagerRouteErrors outputManager, ManifestRecorder experimentalManifest, ExperimentExecutionArguments expArgs, LogReporter logReporter ) throws ExperimentExecutionException;

	@Override
	public boolean useDetailedLogging() {
		return forceDetailedLogging;
	}

	public boolean isForceDetailedLogging() {
		return forceDetailedLogging;
	}
	
	// Runs the experiment on all files
	private void initBeforeDo( BoundOutputManagerRouteErrors bom, boolean debugMode ) throws IOException {
		
		// Now let's delete existing files if we want
		getOutput().deleteExstExpQuietly( getExperimentIdentifier().identifier(), debugMode );
		UpdateLog4JOutputManager.updateLog4J(bom);
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
	
	public LogReporterBean getLogReporter() {
		return logReporterBean;
	}


	public void setLogReporter(LogReporterBean logReporter) {
		this.logReporterBean = logReporter;
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
