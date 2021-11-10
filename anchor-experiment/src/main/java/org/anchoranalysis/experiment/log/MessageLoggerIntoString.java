package org.anchoranalysis.experiment.log;

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.log.MessageLogger;

/**
 * Logs error messages into a {@link String} via {@link StringBuilder}.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class MessageLoggerIntoString implements MessageLogger {

    private final StringBuilder builder;

    @Override
    public void log(String message) {
        builder.append(message);
        builder.append(System.lineSeparator());
    }

    @Override
    public void logFormatted(String formatString, Object... args) {
        log(String.format(formatString, args));
    }
}
