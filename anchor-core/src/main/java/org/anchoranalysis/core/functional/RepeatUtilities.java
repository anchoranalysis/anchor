package org.anchoranalysis.core.functional;

import org.anchoranalysis.core.functional.function.CheckedBooleanSupplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utilities for repeating operations a certain number of times.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RepeatUtilities {

    /**
     * Repeats an operation a number of times.
     * 
     * @param numberTimes how many times to repeat the operation
     * @param operation the operation
     */
    public static void repeat(int numberTimes, Runnable operation) {
        for( int i=0; i<numberTimes; i++) {
            operation.run();
        }
    }
    
    /**
     * Repeats an operation until a true value is returned.
     * 
     * @param maxNumberTimes the maximum number of times to repeat if no true value is returned.
     * @param supplier the operation to repeat that returns true or false
     * @param <E> an exception that may be thrown by the supplier
     * @return true if at least one operation returned true, or false if no operation did.
     * @throws E if {@code supplier} throws an exception.
     */
    public static <E extends Exception> boolean repeatUntil(int maxNumberTimes, CheckedBooleanSupplier<E> supplier) throws E {
        for( int i=0; i<maxNumberTimes; i++) {
            if (supplier.getAsBoolean()) {
                return true;
            }
        }
        return false;
    }
}
