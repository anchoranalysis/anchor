/* (C)2020 */
package org.anchoranalysis.core.property;

import org.anchoranalysis.core.event.IRoutableEventSourceObject;
import org.anchoranalysis.core.event.RoutableEvent;
import org.anchoranalysis.core.event.RoutableListener;
import org.anchoranalysis.core.event.RoutableListenerAdapter;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;

/**
 * @author Owen Feehan
 * @param <T> property-value-type
 */
public class PropertyValueReceivableAdapter<T>
        extends RoutableListenerAdapter<PropertyValueChangeEvent<T>> {

    public PropertyValueReceivableAdapter(
            final IRoutableEventSourceObject eventSource, IPropertyValueReceivable<T> s) {
        s.addPropertyValueChangeListener(
                (PropertyValueChangeEvent<T> evt) -> triggerEventOccurred(eventSource, evt));
    }

    @SuppressWarnings("unchecked")
    private void triggerEventOccurred(
            IRoutableEventSourceObject eventSource, PropertyValueChangeEvent<T> evt) {
        for (RoutableListener<PropertyValueChangeEvent<T>> l :
                getList().getListeners(RoutableListener.class)) {
            l.eventOccurred(new RoutableEvent<>(eventSource, evt));
        }
    }
}
