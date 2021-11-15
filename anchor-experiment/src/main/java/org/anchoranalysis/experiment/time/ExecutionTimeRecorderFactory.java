package org.anchoranalysis.experiment.time;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.core.time.ExecutionTimeRecorderIgnore;

/**
 * Creates an instance of {@link ExecutionTimeRecorder} depending in what outputs are enabled.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExecutionTimeRecorderFactory {

    /**
     * Creates either a {@link ExecutionTimeRecorder} that records times, or ignores the recording
     * depending on whether any output is enabled that needs the execution-time.
     *
     * @param executionTimeOutputsEnabled whether any relevant output is enabled for the
     *     execution-times.
     * @return a corresponding instance of {@link ExecutionTimeRecorder}, newly created if {@code
     *     executionTimeOutputsEnabled==true}.
     */
    public static ExecutionTimeRecorder create(boolean executionTimeOutputsEnabled) {
        if (executionTimeOutputsEnabled) {
            return new RunningSumRecorder();
        } else {
            return ExecutionTimeRecorderIgnore.instance();
        }
    }
}
