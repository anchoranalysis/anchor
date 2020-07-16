/* (C)2020 */
package org.anchoranalysis.io.generator.combined;

import java.util.Optional;

/**
 * A simple container expressing a name-value pair
 *
 * @author Owen Feehan
 * @param <V> value-type
 */
public class OptionalNameValue<V> {

    private Optional<String> name;

    private V value;

    public OptionalNameValue(Optional<String> name, V value) {
        super();
        this.name = name;
        this.value = value;
    }

    public Optional<String> getName() {
        return name;
    }

    public V getValue() {
        return value;
    }
}
