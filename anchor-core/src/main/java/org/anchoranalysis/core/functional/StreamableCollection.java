package org.anchoranalysis.core.functional;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

/**
 * As a stream can only be used once, this data-structure allows for repeatedly getting streams from
 * a collection
 *
 * @author Owen Feehan
 * @param<T> stream-type
 */
@AllArgsConstructor
@Accessors(fluent = true)
public class StreamableCollection<T> {

    private final Supplier<Stream<T>> supplier;

    /**
     * Constructor - create for a particular collection
     *
     * @param collection the collection
     */
    public StreamableCollection(Collection<T> collection) {
        supplier = collection::stream;
    }

    public Stream<T> stream() {
        return supplier.get();
    }
}
