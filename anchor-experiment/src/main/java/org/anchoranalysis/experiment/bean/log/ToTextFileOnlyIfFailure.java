/* (C)2020 */
package org.anchoranalysis.experiment.bean.log;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.log.reporter.FailureOnlyMessageLogger;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

/**
 * Logs to a text file like with {@link org.anchoranalysis.experiment.bean.log.ToTextFile} but the
 * log is ONLY written if a failure occurs in the experiment.
 *
 * <p>If no failure, occurs no file is outputted on the filesystem at all.
 *
 * <p>This is a convenient means of avoiding write and sotrage costs of files of perhaps minimal
 * value if nothing went wrong.
 *
 * @author Owen Feehan
 */
public class ToTextFileOnlyIfFailure extends ToTextFileBase {

    @Override
    public StatefulMessageLogger create(
            BoundOutputManager outputManager,
            ErrorReporter errorReporter,
            ExperimentExecutionArguments arguments,
            boolean detailedLogging) {
        return new FailureOnlyMessageLogger(getOutputName(), outputManager, errorReporter);
    }
}
