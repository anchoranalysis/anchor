package org.anchoranalysis.core.time;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.core.log.Logger;

/**
 * Context objects for general operations that allow for logging and recording execution time.
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor
public class OperationContext {

    /** Allows for the execution time of certain operations to be recorded. */
    private final ExecutionTimeRecorder executionTimeRecorder;

    /**
     * Where to write informative messages to, and and any non-fatal errors (fatal errors are throw
     * as exceptions).
     */
    private final Logger logger;

    /**
     * Creates with a {@link Logger} ignoring execution-times.
     *
     * @param logger the logger.
     */
    public OperationContext(Logger logger) {
        this(ExecutionTimeRecorderIgnore.instance(), logger);
    }
}
