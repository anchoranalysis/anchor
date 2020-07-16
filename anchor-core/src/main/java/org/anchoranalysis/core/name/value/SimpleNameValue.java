/* (C)2020 */
package org.anchoranalysis.core.name.value;

/**
 * A simple container expressing a name-value pair
 *
 * @author Owen Feehan
 * @param <V> value-type
 */
public final class SimpleNameValue<V> implements NameValue<V> {

    private String name;

    private V value;

    public SimpleNameValue(String name, V value) {
        super();
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public void setValue(V value) {
        this.value = value;
    }
}
