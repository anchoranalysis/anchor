package org.anchoranalysis.experiment.task;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.error.reporter.ErrorReporterIntoLog;
import org.anchoranalysis.core.log.MessageLogger;

/**
 * An error-reporter that replaces particular errors/exceptions with user-friendly messages.
 * 
 * @author Owen Feehan
 *
 */
public class ErrorReporterForTask implements ErrorReporter {

    private ErrorReporter delegate;
    
    public ErrorReporterForTask( MessageLogger logger ) {
        delegate = new ErrorReporterIntoLog(logger);
    }

    @Override
    public void recordError(Class<?> classOriginating, Throwable exc) {
        if (exc instanceof OutOfMemoryError) {
            // Special behaviour for out-of-memory errors
            delegate.recordError(classOriginating, "There is insufficient memory available for this task. Consider allocating more memory to the Java Virtual Machine with the -XmX argument.");
        } else {
            delegate.recordError(classOriginating, exc);
        }
    }

    @Override
    public void recordError(Class<?> classOriginating, String errorMsg) {
        delegate.recordError(classOriginating, errorMsg);
    }
}
