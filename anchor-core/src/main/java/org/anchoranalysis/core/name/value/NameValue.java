/* (C)2020 */
package org.anchoranalysis.core.name.value;

/**
 * An object that has an associated textual name and value
 *
 * @author Owen Feehan
 * @param <T> value-type
 */
public interface NameValue<T> {

    /** The associated name */
    String getName();

    /** @link getName() */
    void setName(String name);

    /** The associated value */
    T getValue();

    /** @link getValue() */
    void setValue(T item);
}
