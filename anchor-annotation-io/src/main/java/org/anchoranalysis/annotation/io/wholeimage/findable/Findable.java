/* (C)2020 */
package org.anchoranalysis.annotation.io.wholeimage.findable;

import java.util.Optional;
import org.anchoranalysis.core.log.Logger;

/**
 * An object that can be Found or Not-Found
 *
 * @author Owen Feehan
 * @param <T> object-type
 */
public interface Findable<T> {

    /**
     * Returns the found object (or empty() if it's not found..... and in this case logs a message
     * describing what went wrong)
     *
     * @param name
     * @param logger
     * @return true if successful, false if not-found
     */
    Optional<T> getFoundOrLog(String name, Logger logger);
}
