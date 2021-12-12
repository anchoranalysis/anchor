package org.anchoranalysis.experiment.time;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.math.arithmetic.RunningSum;

/**
 * Wraps a {@link RunningSum} with an additional variable, indicating the total number of parent
 * operations.
 *
 * <p>{@link #hashCode()} and {@link #equals(Object)} delegate to the underlying {@link RunningSum}.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class RunningSumParented {

    // START REQUIRED ARGUMENTS
    /** The number of parent operations. */
    @Getter private final int numberParentOperations;
    // END REQUIRED ARGUMENTS

    @Getter private RunningSum runningSum = new RunningSum();

    public int hashCode() {
        return runningSum.hashCode();
    }

    public boolean equals(Object obj) {
        return runningSum.equals(obj);
    }
}
