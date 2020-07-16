/* (C)2020 */
package org.anchoranalysis.annotation.io.wholeimage.findable;

import java.util.Optional;
import lombok.Value;
import org.anchoranalysis.core.log.Logger;

/**
 * A positive-result when an object is found
 *
 * @author Owen Feehan
 * @param <T>
 */
@Value
public class Found<T> implements Findable<T> {

    /** The found object */
    private T object;

    @Override
    public Optional<T> getFoundOrLog(String name, Logger logger) {
        return Optional.of(object);
    }
}
