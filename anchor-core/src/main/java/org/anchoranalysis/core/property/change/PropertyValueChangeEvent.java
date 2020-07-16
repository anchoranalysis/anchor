/* (C)2020 */
package org.anchoranalysis.core.property.change;

import java.util.EventObject;

public class PropertyValueChangeEvent<T> extends EventObject {

    /** */
    private static final long serialVersionUID = -2465477270335155011L;

    private transient T value;
    private boolean adjusting;

    public PropertyValueChangeEvent(Object source, T value, boolean adjusting) {
        super(source);
        this.value = value;
        this.adjusting = adjusting;
    }

    public T getValue() {
        return value;
    }

    public boolean getAdjusting() {
        return this.adjusting;
    }
}
