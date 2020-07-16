/* (C)2020 */
package org.anchoranalysis.core.property;

import org.anchoranalysis.core.property.change.PropertyValueChangeListener;

public interface IPropertyValueReceivable<T> {

    void addPropertyValueChangeListener(PropertyValueChangeListener<T> l);

    void removePropertyValueChangeListener(PropertyValueChangeListener<T> l);
}
