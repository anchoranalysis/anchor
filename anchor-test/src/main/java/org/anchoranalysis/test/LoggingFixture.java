/* (C)2020 */
package org.anchoranalysis.test;

import static org.mockito.Mockito.mock;

import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.log.MessageLogger;

/**
 * Fixtures to create loggers that don't output anything
 *
 * @author Owen Feehan
 */
public class LoggingFixture {

    private LoggingFixture() {}

    /** A {@link MessageLogger} that doesn't output anything */
    public static MessageLogger suppressedLogReporter() {
        return mock(MessageLogger.class);
    }

    /** A {@link Logger} that doesn't output anything */
    public static Logger suppressedLogErrorReporter() {
        return new Logger(suppressedLogReporter());
    }
}
