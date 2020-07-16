/* (C)2020 */
package org.anchoranalysis.experiment.bean.log;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

/**
 * Switches between two log-reporters depending on whether detailed logging is switched on or not
 *
 * @author Owen Feehan
 */
public class SwitchDetailedLogging extends LoggingDestination {

    // START BEAN PROPERTIES
    /** Logger to use when detailed-logging is on */
    @BeanField @Getter @Setter private LoggingDestination whenDetailed;

    /** Logger to use when detailed-logging is off */
    @BeanField @Getter @Setter private LoggingDestination whenNot;
    // END BEAN PROPERTIES

    @Override
    public StatefulMessageLogger create(
            BoundOutputManager bom,
            ErrorReporter errorReporter,
            ExperimentExecutionArguments arguments,
            boolean detailedLogging) {
        if (detailedLogging) {
            return whenDetailed.create(bom, errorReporter, arguments, detailedLogging);
        } else {
            return whenNot.create(bom, errorReporter, arguments, detailedLogging);
        }
    }
}
