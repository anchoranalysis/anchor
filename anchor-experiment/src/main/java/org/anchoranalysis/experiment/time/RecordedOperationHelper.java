package org.anchoranalysis.experiment.time;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.time.RecordedExecutionTimes.RecordedOperation;
import org.anchoranalysis.math.arithmetic.RunningSum;

/**
 * Helps create {@link RecordedOperation} from a {@link RunningSum}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class RecordedOperationHelper {

    /**
     * Creates the operation.
     *
     * @param operationIdentifier the identifier.
     * @param runningSum the running sum.
     * @return the operation.
     */
    public static RecordedOperation create(String operationIdentifier, RunningSum runningSum) {
        return new RecordedOperation(
                operationIdentifier,
                runningSum.mean(),
                runningSum.getSum(),
                (int) runningSum.getCount());
    }
}
