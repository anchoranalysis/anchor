package org.anchoranalysis.experiment.bean.logreporter;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.log.reporter.StatefulLogReporter;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

/**
 * Switches between two log-reporters depending on whether detailed logging is switched on or not
 * 
 * @author owen
 *
 */
public class LogReporterBeanSwitchDetailedLogging extends LogReporterBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private LogReporterBean detailed;
	
	@BeanField
	private LogReporterBean notDetailed;
	// END BEAN PROPERTIES

	@Override
	public StatefulLogReporter create(BoundOutputManager bom, ErrorReporter errorReporter,
			ExperimentExecutionArguments expArgs, boolean detailedLogging) {
		if (detailedLogging) {
			return detailed.create(bom, errorReporter, expArgs, detailedLogging);
		} else {
			return notDetailed.create(bom, errorReporter, expArgs, detailedLogging);
		}
	}

	public LogReporterBean getDetailed() {
		return detailed;
	}

	public void setDetailed(LogReporterBean detailed) {
		this.detailed = detailed;
	}

	public LogReporterBean getNotDetailed() {
		return notDetailed;
	}

	public void setNotDetailed(LogReporterBean notDetailed) {
		this.notDetailed = notDetailed;
	}

}
