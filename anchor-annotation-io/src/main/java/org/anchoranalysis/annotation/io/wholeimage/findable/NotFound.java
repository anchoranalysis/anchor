/* (C)2020 */
package org.anchoranalysis.annotation.io.wholeimage.findable;

import java.nio.file.Path;
import java.util.Optional;
import lombok.Value;
import org.anchoranalysis.core.log.Logger;

/**
 * A negative-result when an object is NOT found at a particular location
 *
 * @author Owen Feehan
 * @param <T>
 */
@Value
public class NotFound<T> implements Findable<T> {

    /** the path an object was not found at. */
    private final Path path;

    private final String reason;

    @Override
    public Optional<T> getFoundOrLog(String name, Logger logger) {

        logger.messageLogger().logFormatted("Cannot find %s: %s at %s", name, reason, path);

        return Optional.empty();
    }
}
