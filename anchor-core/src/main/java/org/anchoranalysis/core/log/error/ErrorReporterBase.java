package org.anchoranalysis.core.log.error;

import java.util.Optional;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.friendly.HasFriendlyErrorMessage;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * Utility base class for common error-reporting formatting.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public abstract class ErrorReporterBase implements ErrorReporter {

    // START REQUIRED ARGUMENTS
    /** Writes a particular message to the log. */
    private final Consumer<String> logOperation;
    // END REQUIRED ARGUMENTS

    /** True if at least one warning has been outputted. */
    private boolean warningOccurred = false;

    @Override
    public void recordError(Class<?> classOriginating, Throwable exc) {
        recordError(classOriginating, Optional.empty(), exc);
    }

    @Override
    public void recordError(Class<?> classOriginating, String message) {
        try {
            logWithBanner(message, classOriginating);
        } catch (Exception e) {
            log("An error occurred while writing an error: " + e.toString());
        }
    }

    @Override
    public void recordError(Class<?> classOriginating, String message, Throwable exc) {
        recordError(classOriginating, Optional.of(message), exc);
    }

    @Override
    public void recordWarning(String message) {
        warningOccurred = true;
        try {
            logWithBanner(message, true);
        } catch (Exception e) {
            log("An error occurred while writing an warning: " + e.toString());
        }
    }

    @Override
    public boolean hasWarningOccurred() {
        return warningOccurred;
    }

    private void logWithBanner(String logMessage, boolean warning) {
        log(DecorateMessage.decorate(logMessage, warning));
    }

    private void logWithBanner(String logMessage, Class<?> classOriginating) {
        log(DecorateMessage.decorate(logMessage + classMessage(classOriginating), false));
    }

    private static String classMessage(Class<?> c) {
        return String.format(
                "%nThe error occurred when executing a method in class %s", c.getName());
    }

    private void recordError(Class<?> classOriginating, Optional<String> message, Throwable exc) {
        // Special behaviour if it's a friendly exception
        if (exc instanceof HasFriendlyErrorMessage) {
            HasFriendlyErrorMessage eCast = (HasFriendlyErrorMessage) exc;
            String lines = maybePrepend(message, eCast.friendlyMessageHierarchy());
            logWithBanner(lines, false);
        } else {
            try {
                logWithBanner(
                        maybePrepend(message, exc.toString())
                                + System.lineSeparator()
                                + ExceptionUtils.getFullStackTrace(exc),
                        classOriginating);
            } catch (Exception e) {
                log("An error occurred while writing an error: " + e.toString());
            }
        }
    }

    /** Writes a single message to the log. */
    private void log(String message) {
        logOperation.accept(message);
    }

    /**
     * Prepends {@link prefix} to {@link string} if it is defined, inserting a newline in between.
     */
    private static String maybePrepend(Optional<String> prefix, String string) {
        if (prefix.isPresent()) {
            return prefix.get() + System.lineSeparator() + string;
        } else {
            return string;
        }
    }
}
