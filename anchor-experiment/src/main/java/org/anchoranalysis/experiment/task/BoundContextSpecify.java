package org.anchoranalysis.experiment.task;

import java.nio.file.Path;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.log.reporter.StatefulLogReporter;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

class BoundContextSpecify implements BoundIOContext {
	
	private ExperimentExecutionArguments experimentArguments;
	private BoundOutputManagerRouteErrors outputManager;
	
	private StatefulLogReporter logReporter;
	private LogErrorReporter logger;	// Always related to the above two fields
	
	public BoundContextSpecify(
		ExperimentExecutionArguments experimentArguments,
		BoundOutputManagerRouteErrors outputManager,
		StatefulLogReporter logReporter,
		ErrorReporter errorReporter
	) {
		super();
		this.experimentArguments = experimentArguments;
		this.outputManager = outputManager;
		
		this.logReporter = logReporter;
		this.logger = new LogErrorReporter(logReporter, errorReporter);
	}
	
	@Override
	public Path getModelDirectory() {
		return experimentArguments.getModelDirectory();
	}

	@Override
	public boolean isDebugEnabled() {
		return experimentArguments.isDebugEnabled();
	}
	
	@Override
	public BoundOutputManagerRouteErrors getOutputManager() {
		return outputManager;
	}
	
	@Override
	public LogErrorReporter getLogger() {
		return logger;
	}

	/** Exposed as {@link StatefulLogReporter} rather than as {@link LogReporter} that is found in {@link LogErrorReporter} */
	public StatefulLogReporter getStatefulLogReporter() {
		return logReporter;
	}

	public ExperimentExecutionArguments getExperimentArguments() {
		return experimentArguments;
	}
}