package org.anchoranalysis.io.manifest.finder;

import java.io.IOException;
import java.util.Optional;
import org.anchoranalysis.core.cache.CachedSupplier;

/**
 * Supplies a serialized-object (if it exists)
 *
 * @author Owen Feehan
 * @param <T> type of serialized object
 */
@FunctionalInterface
public interface SerializedObjectSupplier<T> {

    Optional<T> get() throws IOException;

    public static <T> SerializedObjectSupplier<T> cache(SerializedObjectSupplier<T> supplier) {
        return CachedSupplier.cache(supplier::get)::get;
    }
}
