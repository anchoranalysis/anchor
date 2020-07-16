/* (C)2020 */
package org.anchoranalysis.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.name.value.NameValue;

/**
 * A bean with an associated (textual) name
 *
 * @author Owen Feehan
 * @param <T> item type
 */
@AllArgsConstructor
@NoArgsConstructor
public class NamedBean<T extends AnchorBean<?>> extends NullParamsBean<NamedBean<T>>
        implements NameValue<T> {

    // START BEAN PROPERTIES
    /** The name associated with {@code item} */
    @BeanField @Getter @Setter private String name;

    /** The item that is to be named (i.e. the underlying bean) */
    @BeanField @Getter @Setter private T item;
    // END BEAN PROPERTIES

    @Override
    public String toString() {
        return name;
    }

    @Override
    public T getValue() {
        return item;
    }

    @Override
    public void setValue(T item) {
        this.item = item;
    }
}
