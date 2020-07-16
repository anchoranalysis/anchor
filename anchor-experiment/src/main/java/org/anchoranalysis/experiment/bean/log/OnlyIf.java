/* (C)2020 */
package org.anchoranalysis.experiment.bean.log;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.bean.require.RequireArguments;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

/**
 * Logs messages to a particular location ONLY if certain conditions are fulfilled.
 *
 * @author Owen Feehan
 */
public class OnlyIf extends LoggingDestination {

    // START BEAN PROPERTIES
    /** The logger to use if conditions are fulfilled */
    @BeanField @Getter @Setter private LoggingDestination log;

    /** The conditions that must be fulfilled */
    @BeanField @Getter @Setter private RequireArguments requireArguments;
    // END BEAN PROPERTIES

    @Override
    public StatefulMessageLogger create(
            BoundOutputManager outputManager,
            ErrorReporter errorReporter,
            ExperimentExecutionArguments arguments,
            boolean detailedLogging) {
        if (requireArguments.hasAllRequiredArguments(arguments.isDebugModeEnabled())) {
            return log.create(outputManager, errorReporter, arguments, detailedLogging);
        } else {
            return new StatefulNullMessageLogger();
        }
    }
}
