package org.anchoranalysis.core.system;

/**
 * An implementation of {@link ExecutionTimeRecorder} that is a simple placeholder that does
 * nothing.
 *
 * @author Owen Feehan
 */
public class ExecutionTimeRecorderIgnore implements ExecutionTimeRecorder {

    private static ExecutionTimeRecorder instance = null;

    /**
     * Singleton instance.
     *
     * @return the instance.
     */
    public static ExecutionTimeRecorder instance() {
        if (instance == null) {
            instance = new ExecutionTimeRecorderIgnore();
        }
        return instance;
    }

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
