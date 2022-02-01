package org.anchoranalysis.core.collection;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.checked.CheckedBiConsumer;
import org.anchoranalysis.core.functional.checked.CheckedConsumer;

/**
 * Like a {@link MapCreate} but with an associated counter for each element.
 *
 * <p>The counter is initially incremented, and decremented each time an element is accessed.
 *
 * <p>When the counter reaches 0 for a particular element, a function is called on the element, and
 * the element is removed from the map.
 *
 * <p>The actually element is created lazily, only when first needed.
 *
 * <p>An element may never be null.
 *
 * <p>This structure is thread-safe.
 *
 * @author Owen Feehan
 * @param <K> identifier (key)-type
 * @param <V> value-type
 */
public class MapCreateCountdown<K, V> {

    /**
     * An entry in the map with an associated count.
     *
     * <p>It also retains an ability to create an element of type {@code V} lazily.
     *
     * @author Owen Feehan
     * @param <V> element-type
     */
    @RequiredArgsConstructor
    private static class ElementWithCount<V> {

        // START REQUIRED ARGUMENTS
        /** How to create a new element, called when needed. */
        private final Supplier<V> createNewElement;
        // END REQUIRED ARGUMENTS

        /** The reference count. */
        @Getter private int count = 0;

        /** The element if created, otherwise null. */
        private V element;

        /** Increments the current count. */
        public void increment() {
            count++;
        }

        /**
         * Gets the element, creating if necessary, and decrementing the count.
         *
         * @return the associated element, created if needed.
         */
        public V getElementDecrement() {
            if (element == null) {
                element = createNewElement.get();
            }
            count--;
            return element;
        }
    }

    /**
     * Called to clean up elements, before they are deleted from the map, when the count becomes 0.
     */
    private final CheckedBiConsumer<K, V, OperationFailedException> cleanUpElement;

    /** The underlying map. */
    private MapCreate<K, ElementWithCount<V>> map;

    /**
     * Creates without a comparator, using a {@link HashMap} internally.
     *
     * @param createNewElement called as necessary to create a new element in the tree.
     * @param cleanUpElement called on an element when its reference count reaches 0, after it is
     *     removed from the map.
     */
    public MapCreateCountdown(
            Supplier<V> createNewElement,
            CheckedBiConsumer<K, V, OperationFailedException> cleanUpElement) {
        this.map = new MapCreate<>(() -> new ElementWithCount<>(createNewElement));
        this.cleanUpElement = cleanUpElement;
    }

    /**
     * Increments the entry associated with {@code key}, creating a counter for it if none already
     * exists.
     *
     * @param key the key.
     */
    public synchronized void increment(K key) {
        ElementWithCount<V> element = map.computeIfAbsent(key);
        element.increment();
    }

    /**
     * The keys in the map.
     *
     * @return a set view of the keys contained in this map.
     */
    public synchronized Set<K> keySet() {
        return map.keySet();
    }

    /**
     * Runs an operation on the element, creating if necessary, and decrementing the count.
     *
     * @param key the key in the map.
     * @param operation the operation to run on {@code key}.
     * @throws OperationFailedException if the key does not already exist.
     * @throws OperationFailedException if thown by {@code operation}, of if {@code key} does not
     *     exist in the map.
     */
    public void processElementDecrement(
            K key, CheckedConsumer<V, OperationFailedException> operation)
            throws OperationFailedException {

        int count = -1;
        V element;

        synchronized (this) {
            ElementWithCount<V> withCount = map.get(key);
            if (withCount == null) {
                throw new OperationFailedException("No entry exists in map for key: " + key);
            }
            element = withCount.getElementDecrement();

            operation.accept(element);
            count = withCount.getCount();

            if (count == 0) {
                map.remove(key, withCount);
            }
        }

        if (count == 0) {
            // remove from map and run operation
            cleanUpElement.accept(key, element);
        }
    }
}
