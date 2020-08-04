package org.anchoranalysis.image.object.properties;

import java.util.function.Predicate;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class ObjectCollectionWithPropertiesFactory {

    /**
     * Creates a new collection by filtering an iterable and then mapping it to {@link ObjectWithProperties}
     *
     * @param <T> type that will be mapped to {@link ObjectWithProperties}
     * @param <E> exception-type that may be thrown during mapping
     * @param iterable incoming collection to be mapped
     * @param mapFunc function for mapping
     * @return a newly created {@link ObjectCollectionWithProperties}
     * @throws E if thrown by <code>mapFunc</code>
     */
    public static <T, E extends Exception> ObjectCollectionWithProperties filterAndMapFrom(
            Iterable<T> iterable,
            Predicate<T> predicate,
            CheckedFunction<T, ObjectWithProperties, E> mapFunc)
            throws E {
        ObjectCollectionWithProperties out = new ObjectCollectionWithProperties();
        for (T item : iterable) {

            if (!predicate.test(item)) {
                continue;
            }

            out.add(mapFunc.apply(item));
        }
        return out;
    }
}
