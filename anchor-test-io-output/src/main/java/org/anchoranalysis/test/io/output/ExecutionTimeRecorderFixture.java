package org.anchoranalysis.test.io.output;

import org.anchoranalysis.core.system.ExecutionTimeRecorder;

/**
 * Dummy implementation of {@link ExecutionTimeRecorder} that performs no operations.
 *
 * @author Owen Feehan
 */
class ExecutionTimeRecorderFixture implements ExecutionTimeRecorder {

    @Override
    public void recordExecutionTime(String operationIdentifier, long millis) {
        // NOTHING TO DO
    }

    @Override
    public void recordExecutionTime(
            String operationIdentifierFirst, String operationIdentiferSubsequent, long millis) {
        // NOTHING TO DO
    }
}
