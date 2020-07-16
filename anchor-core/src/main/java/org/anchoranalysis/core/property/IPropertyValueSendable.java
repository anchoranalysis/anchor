/* (C)2020 */
package org.anchoranalysis.core.property;

@FunctionalInterface
public interface IPropertyValueSendable<T> {

    void setPropertyValue(T value, boolean adjusting);
}
