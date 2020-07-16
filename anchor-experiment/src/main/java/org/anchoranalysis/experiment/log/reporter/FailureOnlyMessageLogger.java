/* (C)2020 */
package org.anchoranalysis.experiment.log.reporter;

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

/**
 * Writes text to a file, but only if close is called with a successful=true
 *
 * <p>The text cannot be written immediately, so is saved until close() is called.
 *
 * @author feehano
 */
@RequiredArgsConstructor
public class FailureOnlyMessageLogger implements StatefulMessageLogger {

    // START REQUIRED ARGUMENTS
    private final String outputName;
    private final BoundOutputManager outputManager;
    private final ErrorReporter errorReporter;
    // END REQUIRED ARGUMENTS

    private StringBuilder sb;

    @Override
    public void log(String message) {
        sb.append(message);
        sb.append(System.lineSeparator());
    }

    @Override
    public void logFormatted(String formatString, Object... args) {
        log(String.format(formatString, args));
    }

    @Override
    public void start() {
        sb = new StringBuilder();
    }

    @Override
    public void close(boolean successful) {
        if (!successful) {
            writeStringToFile(sb.toString());
        }
    }

    private void writeStringToFile(String message) {

        try {
            OptionalUtilities.ifPresent(
                    TextFileLogHelper.createOutput(outputManager, outputName),
                    output -> {
                        output.start();
                        output.getWriter().append(message);
                        output.end();
                    });

        } catch (AnchorIOException e) {
            errorReporter.recordError(MessageLogger.class, e);
        }
    }
}
