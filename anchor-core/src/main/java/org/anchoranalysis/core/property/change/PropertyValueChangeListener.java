/* (C)2020 */
package org.anchoranalysis.core.property.change;

import java.util.EventListener;

@FunctionalInterface
public interface PropertyValueChangeListener<T> extends EventListener {
    public void propertyValueChanged(PropertyValueChangeEvent<T> evt);
}
