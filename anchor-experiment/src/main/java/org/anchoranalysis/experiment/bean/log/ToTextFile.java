/* (C)2020 */
package org.anchoranalysis.experiment.bean.log;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;
import org.anchoranalysis.experiment.log.reporter.TextFileMessageLogger;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

/**
 * Logs to a text-file created in the output-directory under a particular name.
 *
 * @author Owen Feehan
 */
public class ToTextFile extends ToTextFileBase {

    @Override
    public StatefulMessageLogger create(
            BoundOutputManager bom,
            ErrorReporter errorReporter,
            ExperimentExecutionArguments arguments,
            boolean detailedLogging) {
        return new TextFileMessageLogger(getOutputName(), bom, errorReporter);
    }
}
